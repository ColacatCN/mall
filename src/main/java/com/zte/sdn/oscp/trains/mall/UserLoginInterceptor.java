package com.zte.sdn.oscp.trains.mall;

import com.zte.sdn.oscp.trains.mall.exception.UserLoginException;
import com.zte.sdn.oscp.trains.mall.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.CURRENT_USER;

public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(CURRENT_USER);
        if (user == null) {
            throw new UserLoginException();
        }
        return true;
    }
}
