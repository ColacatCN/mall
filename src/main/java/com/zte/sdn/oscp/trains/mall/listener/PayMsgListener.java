package com.zte.sdn.oscp.trains.mall.listener;

import com.google.gson.Gson;
import com.zte.sdn.oscp.trains.mall.pojo.PayInfo;
import com.zte.sdn.oscp.trains.mall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.PAY_SUCCESS;

@Component
@RabbitListener(queues = "payNotify")
@Slf4j
public class PayMsgListener {

    @Autowired
    private IOrderService orderService;

    @RabbitHandler
    public void process(String msg) {
        log.info("【已接收到消息】==> {}", msg);
        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        if (PAY_SUCCESS.equals(payInfo.getPlatformStatus())) {
            orderService.paid(payInfo.getOrderNo());
        }
    }
}
