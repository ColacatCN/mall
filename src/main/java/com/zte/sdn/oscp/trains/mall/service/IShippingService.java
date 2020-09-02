package com.zte.sdn.oscp.trains.mall.service;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.form.ShippingForm;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

import java.util.Map;

public interface IShippingService {

    ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm shippingForm);

    ResponseVo delete(Integer uid, Integer shippingId);

    ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm);

    ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);
}
