package com.zte.sdn.oscp.trains.mall.exception;

import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.NEED_LOGIN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RuntimeExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseVo handle(RuntimeException e) {
        return ResponseVo.error(ERROR, e.getMessage());
    }

    @ExceptionHandler(UserLoginException.class)
    @ResponseBody
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseVo userLoginHandle(RuntimeException e) {
        return ResponseVo.error(NEED_LOGIN);
    }
}
