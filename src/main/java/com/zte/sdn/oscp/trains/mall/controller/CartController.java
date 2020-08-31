package com.zte.sdn.oscp.trains.mall.controller;

import com.zte.sdn.oscp.trains.mall.form.CartAddForm;
import com.zte.sdn.oscp.trains.mall.form.CartUpdateForm;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.ICartService;
import com.zte.sdn.oscp.trains.mall.vo.CartVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.CURRENT_USER;

@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping("/carts")
    public ResponseVo<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.list(user.getId());
    }

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.add(user.getId(), cartAddForm);
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(@PathVariable Integer productId,
                                     @RequestBody CartUpdateForm cartUpdateForm,
                                     HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.update(user.getId(), productId, cartUpdateForm);
    }

    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo> delete(@PathVariable Integer productId, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.delete(user.getId(), productId);
    }

    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.selectAll(user.getId());
    }

    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo> unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.unSelectAll(user.getId());
    }

    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer> sum(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
