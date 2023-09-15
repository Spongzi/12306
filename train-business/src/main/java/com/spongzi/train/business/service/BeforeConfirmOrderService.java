package com.spongzi.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.spongzi.train.business.enums.RocketMQTopicEnum;
import com.spongzi.train.business.req.ConfirmOrderDoReq;
import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.exception.BusinessException;
import com.spongzi.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.spongzi.train.common.exception.BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SkTokenService skTokenService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;



    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req) {
        // 校验令牌
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        } else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        String lockKey = req.getTrainCode() + "-" + DateUtil.formatDate(req.getDate());
        RLock lock = null;
        try {
            //  Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
            //  if (Boolean.TRUE.equals(setIfAbsent)) {
            //  LOG.info("恭喜抢到锁了");
            //  } else {
            //  // 只是没有抢到锁，并不知道票抢完了没有，所以提示稍后重试
            //  LOG.info("很遗憾，没有抢到锁");
            //  throw new BusinessException(CONFIRM_ORDER_FLOW_EXCEPTION);
            //  }
            lock = redissonClient.getLock(lockKey);
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);// 看门狗
            if (tryLock) {
                LOG.info("恭喜，抢到锁了！");
            } else {
                // 没有抢到锁，并不知道票抢完了没有，所以提示稍后重试
                LOG.info("很遗憾，没有抢到锁");
                throw new BusinessException(CONFIRM_ORDER_FLOW_EXCEPTION);
            }

            // 发送mq排队购票
            String reqJson = JSON.toJSONString(req);
            LOG.info("排队购票，发送mq开始，消息：{}", reqJson);
            rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
            LOG.info("排队购票，发送mq结束");

        } catch (Exception e) {
            // 最后程序结束释放锁
            LOG.error("购票异常", e);
            throw new BusinessException(e.getMessage());
        }

    }
}
