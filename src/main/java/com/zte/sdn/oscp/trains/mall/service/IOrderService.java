package com.zte.sdn.oscp.trains.mall.service;

import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

public interface IOrderService {

    ResponseVo<OrderVo> create(Integer uid, Integer shippingId);
}
