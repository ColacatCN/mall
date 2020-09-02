package com.zte.sdn.oscp.trains.mall.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.pojo.Shipping;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class ShippingMapperTest extends MallApplicationTests {

    @Autowired
    private ShippingMapper shippingMapper;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void selectByUserIdAndShippingId() {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(1, 4);
        log.info("selectByUserIdAndShippingId = {}", gson.toJson(shipping));
    }
}