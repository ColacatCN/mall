package com.zte.sdn.oscp.trains.mall.service.impl;

import com.zte.sdn.oscp.trains.mall.MallApplicationTests;
import com.zte.sdn.oscp.trains.mall.enums.RoleEnum;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.IUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    @Autowired
    private IUserService userService;

    @Test
    public void register() {
        User user = new User("Leo", "123456", "leo.zhou.ke@gmail.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }
}
