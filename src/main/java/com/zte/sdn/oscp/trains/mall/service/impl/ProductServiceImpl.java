package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zte.sdn.oscp.trains.mall.dao.ProductMapper;
import com.zte.sdn.oscp.trains.mall.pojo.Product;
import com.zte.sdn.oscp.trains.mall.service.ICategoryService;
import com.zte.sdn.oscp.trains.mall.service.IProductService;
import com.zte.sdn.oscp.trains.mall.vo.ProductDetailVo;
import com.zte.sdn.oscp.trains.mall.vo.ProductVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zte.sdn.oscp.trains.mall.enums.ProductStatusEnum.DELETE;
import static com.zte.sdn.oscp.trains.mall.enums.ProductStatusEnum.OFF_SELL;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_OFF_SELL_OR_DELETE;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);

        }

        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVos = products.stream()
                .map(product -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(product, productVo);
                    return productVo;
                })
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo<>(products);
        pageInfo.setList(productVos);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);

        if (OFF_SELL.getCode().equals(product.getStatus()) || DELETE.getCode().equals(product.getStatus())) {
            return ResponseVo.error(PRODUCT_OFF_SELL_OR_DELETE);
        }

        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return ResponseVo.success(productDetailVo);
    }
}
