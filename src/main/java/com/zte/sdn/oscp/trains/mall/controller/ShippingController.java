package com.zte.sdn.oscp.trains.mall.controller;

import com.zte.sdn.oscp.trains.mall.form.ShippingForm;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.IShippingService;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    @PostMapping("/shippings")
    public ResponseVo add(@Valid @RequestBody ShippingForm shippingForm, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.add(user.getId(), shippingForm);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.delete(user.getId(), shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(@PathVariable Integer shippingId,
                             @Valid @RequestBody ShippingForm shippingForm,
                             HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.update(user.getId(), shippingId, shippingForm);
    }

    @GetMapping("/shippings")
    public ResponseVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                           HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.list(user.getId(), pageNum, pageSize);
    }
}
