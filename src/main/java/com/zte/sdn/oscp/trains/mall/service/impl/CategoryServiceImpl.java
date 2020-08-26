package com.zte.sdn.oscp.trains.mall.service.impl;

import com.zte.sdn.oscp.trains.mall.dao.CategoryMapper;
import com.zte.sdn.oscp.trains.mall.pojo.Category;
import com.zte.sdn.oscp.trains.mall.service.ICategoryService;
import com.zte.sdn.oscp.trains.mall.vo.CategoryVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zte.sdn.oscp.trains.mall.consts.MallConst.ROOT_PARENT_ID;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<Category> categories = categoryMapper.selectAll();

        List<CategoryVo> categoryVos = categories.stream()
                .filter(category -> ROOT_PARENT_ID.equals(category.getParentId()))
                .map(this::categoryToCategoryVo)
                .collect(Collectors.toList());
        findSubCategory(categoryVos, categories);

        return ResponseVo.success(categoryVos);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> results) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, results, categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> results, List<Category> categories) {
        for (Category category: categories) {
            if (id.equals(category.getParentId())) {
                results.add(category.getId());
                findSubCategoryId(category.getId(), results, categories);
            }
        }
    }

    private void findSubCategory(List<CategoryVo> categoryVos, List<Category> categories) {
        for (CategoryVo categoryVo : categoryVos) {
            List<CategoryVo> subCategoryVos = new ArrayList<>();
            for (Category category : categories) {
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = categoryToCategoryVo(category);
                    subCategoryVos.add(subCategoryVo);
                }
                categoryVo.setSubCategories(subCategoryVos);
                findSubCategory(subCategoryVos, categories);
            }
        }
    }

    private CategoryVo categoryToCategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
