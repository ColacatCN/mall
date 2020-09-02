package com.zte.sdn.oscp.trains.mall.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class ProductMapperTest extends MallApplicationTests {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private ProductMapper productMapper;

    @Test
    public void selectAll() {
        List<Product> products = productMapper.selectByProductIdList(Arrays.asList(26, 27));
        log.info("result = {}", gson.toJson(products));
    }
}
