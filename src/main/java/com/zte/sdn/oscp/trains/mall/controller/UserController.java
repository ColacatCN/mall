package com.zte.sdn.oscp.trains.mall.controller;

import com.zte.sdn.oscp.trains.mall.form.UserLoginForm;
import com.zte.sdn.oscp.trains.mall.form.UserRegisterForm;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.IUserService;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.Objects;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.CURRENT_USER;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PARAM_ERROR;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("注册提交的参数有误，{} {}",
                    Objects.requireNonNull(bindingResult.getFieldError()).getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            return ResponseVo.error(PARAM_ERROR,  bindingResult);
        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  BindingResult bindingResult,
                                  HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseVo.error(PARAM_ERROR,  bindingResult);
        }

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        session.setAttribute(CURRENT_USER, userResponseVo.getData());
        return userResponseVo;
    }

    @PostMapping("/user/logout")
    public ResponseVo<User> logout(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();
    }

    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);
    }
}
