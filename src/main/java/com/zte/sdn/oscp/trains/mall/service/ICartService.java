package com.zte.sdn.oscp.trains.mall.service;

import com.zte.sdn.oscp.trains.mall.form.CartAddForm;
import com.zte.sdn.oscp.trains.mall.form.CartUpdateForm;
import com.zte.sdn.oscp.trains.mall.vo.CartVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

public interface ICartService {

    ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm);

    ResponseVo<CartVo> list(Integer uid);

    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);

    ResponseVo<CartVo> selectAll(Integer uid);

    ResponseVo<CartVo> unSelectAll(Integer uid);

    ResponseVo<Integer> sum(Integer uid);
}
