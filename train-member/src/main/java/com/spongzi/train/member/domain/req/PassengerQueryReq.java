package com.spongzi.train.member.domain.req;

import com.spongzi.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}
