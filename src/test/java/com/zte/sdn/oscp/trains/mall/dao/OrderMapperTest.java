package com.zte.sdn.oscp.trains.mall.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class OrderMapperTest extends MallApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void selectByPrimaryKey() {
        Order order = orderMapper.selectByPrimaryKey(1);
        log.info("order = {}", gson.toJson(order));
    }

    @Test
    public void selectByUserId() {
        List<Order> orderList = orderMapper.selectByUserId(1);
        log.info("orderList = {}", gson.toJson(orderList));
    }
}
