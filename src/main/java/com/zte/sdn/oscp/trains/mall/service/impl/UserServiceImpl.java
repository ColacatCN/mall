package com.zte.sdn.oscp.trains.mall.service.impl;

import com.zte.sdn.oscp.trains.mall.dao.UserMapper;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.service.IUserService;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.EMAIL_EXIST;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.USERNAME_EXIST;
import static com.zte.sdn.oscp.trains.mall.enums.RoleEnum.CUSTOMER;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo register(User user) {
        // 1. 校验合法性
        int countOfUsername = userMapper.countByUsername(user.getUsername());
        if (countOfUsername > 0) {
            return ResponseVo.error(USERNAME_EXIST);
        }

        int countOfEmail = userMapper.countByEmail(user.getEmail());
        if (countOfEmail > 0) {
            return ResponseVo.error(EMAIL_EXIST);
        }

        user.setRole(CUSTOMER.getCode());

        // 2. MD5 摘要算法
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        // 3. 写入数据库
        int insertResult = userMapper.insertSelective(user);
        if (insertResult == 0 ) {
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.success();
    }
}
