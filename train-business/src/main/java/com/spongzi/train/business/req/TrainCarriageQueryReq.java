package com.spongzi.train.business.req;

import com.spongzi.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainCarriageQueryReq extends PageReq {

    private String trainCode;

}
