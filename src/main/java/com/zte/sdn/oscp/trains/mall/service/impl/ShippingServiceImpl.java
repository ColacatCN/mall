package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.dao.ShippingMapper;
import com.zte.sdn.oscp.trains.mall.form.ShippingForm;
import com.zte.sdn.oscp.trains.mall.pojo.Shipping;
import com.zte.sdn.oscp.trains.mall.service.IShippingService;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.DELETE_SHIPPING_FAIL;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.UPDATE_SHIPPING_FAIL;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        shipping.setUserId(uid);
        BeanUtils.copyProperties(shippingForm, shipping);

        int row = shippingMapper.insertSelective(shipping);
        if (row == 0) {
            return ResponseVo.error(ERROR);
        } else {
            Map<String, Integer> shippingMap = new HashMap<>();
            shippingMap.put("shippingId", shipping.getId());
            return ResponseVo.success(shippingMap);
        }
    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {
        int row = shippingMapper.deleteByUidAndId(uid, shippingId);
        if (row == 0) {
            return ResponseVo.error(DELETE_SHIPPING_FAIL);
        } else {
            return ResponseVo.success();
        }
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        BeanUtils.copyProperties(shippingForm, shipping);

        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row == 0) {
            return ResponseVo.error(UPDATE_SHIPPING_FAIL);
        } else {
            return ResponseVo.success();
        }
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(uid);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ResponseVo.success(pageInfo);
    }
}
