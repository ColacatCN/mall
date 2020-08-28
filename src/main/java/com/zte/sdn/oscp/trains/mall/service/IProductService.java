package com.zte.sdn.oscp.trains.mall.service;

import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.vo.ProductDetailVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

public interface IProductService {

    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
