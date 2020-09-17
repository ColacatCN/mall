package com.zte.sdn.oscp.trains.mall.controller;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.form.OrderCreateForm;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.IOrderService;
import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.CURRENT_USER;

@RestController
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm orderCreateForm, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.create(user.getId(), orderCreateForm.getShippingId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.list(user.getId(), pageNum, pageSize);
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.detail(user.getId(), orderNo);
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.cancel(user.getId(), orderNo);
    }
}
