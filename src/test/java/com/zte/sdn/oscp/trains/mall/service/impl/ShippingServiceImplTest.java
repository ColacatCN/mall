package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.dao.ShippingMapper;
import com.zte.sdn.oscp.trains.mall.form.ShippingForm;
import com.zte.sdn.oscp.trains.mall.pojo.Shipping;
import com.zte.sdn.oscp.trains.mall.service.IShippingService;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class ShippingServiceImplTest extends MallApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private static final Integer uid = 1;

    @Test
    public void add() {
        ShippingForm shippingForm = new ShippingForm();
        shippingForm.setReceiverName("Leo");
        shippingForm.setReceiverPhone("010");
        shippingForm.setReceiverMobile("13812345678");
        shippingForm.setReceiverProvince("江苏省");
        shippingForm.setReceiverCity("南京");
        shippingForm.setReceiverDistrict("雨花台区");
        shippingForm.setReceiverAddress("景明佳园");
        shippingForm.setReceiverZip("100000");
        ResponseVo<Map<String, Integer>> addResult = shippingService.add(uid, shippingForm);
        log.info("addResult = {}", addResult);
    }

    @Test
    public void delete() {
        ResponseVo deleteResult = shippingService.delete(uid, 8);
        log.info("deleteResult = {}", deleteResult);
    }

    @Test
    public void update() {
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> listResult = shippingService.list(1, 1, 10);
        log.info("listResult = {}", listResult);
    }
}
