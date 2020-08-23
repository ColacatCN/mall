package com.zte.sdn.oscp.trains.mall.dao;

import com.zte.sdn.oscp.trains.mall.pojo.OrderItem;

public interface OrderItemMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}
