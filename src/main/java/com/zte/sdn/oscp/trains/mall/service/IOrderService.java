package com.zte.sdn.oscp.trains.mall.service;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

public interface IOrderService {

    ResponseVo<OrderVo> create(Integer uid, Integer shippingId);

    ResponseVo<PageInfo> list(Integer uid, int pageNum, int pageSize);

    ResponseVo<OrderVo> detail(Integer uid, Long orderNo);

    ResponseVo cancel(Integer uid, Long orderNo);

    void paid(Long orderNo);
}
