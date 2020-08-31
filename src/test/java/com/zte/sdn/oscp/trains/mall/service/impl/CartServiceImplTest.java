package com.zte.sdn.oscp.trains.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.form.CartAddForm;
import com.zte.sdn.oscp.trains.mall.form.CartUpdateForm;
import com.zte.sdn.oscp.trains.mall.service.ICartService;
import com.zte.sdn.oscp.trains.mall.vo.CartVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class CartServiceImplTest extends MallApplicationTests {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private ICartService cartService;

    @Test
    public void list() {
        ResponseVo<CartVo> responseVo = cartService.list(1);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void add() {
        CartAddForm cartAddForm = new CartAddForm();
        cartAddForm.setProductId(26);
        cartAddForm.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(1, cartAddForm);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void update() {
        CartUpdateForm cartUpdateForm = new CartUpdateForm();
        cartUpdateForm.setQuantity(5);
        cartUpdateForm.setSelected(false);
        ResponseVo responseVo = cartService.update(1, 26, cartUpdateForm);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void delete() {
        ResponseVo responseVo = cartService.delete(1, 29);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void selectAll() {
        ResponseVo responseVo = cartService.selectAll(1);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void noSelectAll() {
        ResponseVo responseVo = cartService.unSelectAll(1);
        log.info("list = {}", gson.toJson(responseVo));
    }

    @Test
    public void sum() {
        ResponseVo responseVo = cartService.sum(1);
        log.info("list = {}", gson.toJson(responseVo));
    }
}
