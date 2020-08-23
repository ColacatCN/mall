package com.zte.sdn.oscp.trains.mall.service;

import com.zte.sdn.oscp.trains.mall.pojo.User;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

public interface IUserService {

    /**
     * 注册
     */
    ResponseVo register(User user);

    /**
     * 登录
     */

}
