package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.service.IProductService;
import com.zte.sdn.oscp.trains.mall.vo.ProductVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class ProductServiceImplTest extends MallApplicationTests {

    @Autowired
    private IProductService productService;

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = productService.list(null, 2, 3);
        log.info("responseVo = {}", responseVo);
    }
}
