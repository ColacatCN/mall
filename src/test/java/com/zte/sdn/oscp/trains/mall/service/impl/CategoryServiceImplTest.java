package com.zte.sdn.oscp.trains.mall.service.impl;

import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@Slf4j
public class CategoryServiceImplTest extends MallApplicationTests {

    @Autowired
    private ICategoryService categoryService;

    @Test
    public void findSubCategoryId() {
        Set<Integer> set = new HashSet<>();
        categoryService.findSubCategoryId(100001, set);
        log.info("set = {}", set);
    }
}