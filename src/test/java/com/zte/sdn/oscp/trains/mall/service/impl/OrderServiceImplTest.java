package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.form.CartAddForm;
import com.zte.sdn.oscp.trains.mall.service.ICartService;
import com.zte.sdn.oscp.trains.mall.service.IOrderService;
import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class OrderServiceImplTest extends MallApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void before() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(26);
        cartAddForm.setSelected(true);
        cartService.add(1, cartAddForm);
    }

    @Test
    public void create() {
        ResponseVo<OrderVo> orderVoResponseVo = orderService.create(1, 4);
        log.info("orderVoResponseVo = {}", gson.toJson(orderVoResponseVo));
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> list = orderService.list(1, 2, 2);
        log.info("list = {}", gson.toJson(list));
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> detail = orderService.detail(1, 1599054995061L);
        log.info("detail = {}", gson.toJson(detail));
    }

    @Test
    public void cancel() {
        ResponseVo cancel = orderService.cancel(1, 1599054995061L);
        log.info("cancel = {}", gson.toJson(cancel));
    }
}
