package com.zte.sdn.oscp.trains.mall.service;

import com.zte.sdn.oscp.trains.mall.vo.CategoryVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> results);
}
