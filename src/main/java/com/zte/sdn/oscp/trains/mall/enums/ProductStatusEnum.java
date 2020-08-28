package com.zte.sdn.oscp.trains.mall.enums;

import lombok.Getter;

@Getter
public enum ProductStatusEnum {

    ON_SELL(1),

    OFF_SELL(2),

    DELETE(3),

    ;

    Integer code;

    ProductStatusEnum(Integer code) {
        this.code = code;
    }
}
