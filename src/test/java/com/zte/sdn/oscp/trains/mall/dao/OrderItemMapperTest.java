package com.zte.sdn.oscp.trains.mall.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.pojo.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class OrderItemMapperTest extends MallApplicationTests {

    @Autowired
    private OrderItemMapper orderItemMapper;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void selectByOrderNoSet() {
    }
}
