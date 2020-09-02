package com.zte.sdn.oscp.trains.mall.service.impl;

import com.google.gson.Gson;
import com.zte.sdn.oscp.trains.mall.dao.ProductMapper;
import com.zte.sdn.oscp.trains.mall.form.CartAddForm;
import com.zte.sdn.oscp.trains.mall.form.CartUpdateForm;
import com.zte.sdn.oscp.trains.mall.pojo.Cart;
import com.zte.sdn.oscp.trains.mall.pojo.Product;
import com.zte.sdn.oscp.trains.mall.service.ICartService;
import com.zte.sdn.oscp.trains.mall.vo.CartProductVo;
import com.zte.sdn.oscp.trains.mall.vo.CartVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zte.sdn.oscp.trains.mall.enums.ProductStatusEnum.ON_SELL;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.CART_PRODUCT_NOT_EXIST;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_NOT_EXIST;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_OFF_SELL_OR_DELETE;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_STOCK_ERROR;

@Service
public class CartServiceImpl implements ICartService {

    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";
    private final Gson gson = new Gson();
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        List<Integer> productIdList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            productIdList.add(Integer.valueOf(entry.getKey()));
        }
        Map<Integer, Product> productMap = productMapper.selectByProductIdList(productIdList).stream().
                collect(Collectors.toMap(Product::getId, product -> product));

        CartVo cartVo = new CartVo();
        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            Product product = productMap.get(productId);
            if (product != null) {
                if (!cart.getProductSelected()) {
                    selectAll = false;
                }
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected());
                cartProductVoList.add(cartProductVo);
                if (cart.getProductSelected()) {
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }
            }
            cartTotalQuantity += cart.getQuantity();
        }
        cartVo.setSelectAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);

        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm) {
        Integer quantity = 1;

        // 1. 判断商品是否存在
        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());
        if (product == null) {
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }

        // 2. 商品是否正常在售
        if (!ON_SELL.getCode().equals(product.getStatus())) {
            return ResponseVo.error(PRODUCT_OFF_SELL_OR_DELETE);
        }

        // 3. 商品库存是否充足
        if (0 >= product.getStock()) {
            return ResponseVo.error(PRODUCT_STOCK_ERROR);
        }

        // 4. 写入到 Redis 中
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String redisValue = opsForHash.get(redisKey, String.valueOf(product.getId()));
        Cart cart;
        if (StringUtils.isEmpty(redisValue)) {
            cart = new Cart(product.getId(), quantity, cartAddForm.getSelected());
        } else {
            cart = gson.fromJson(redisValue, Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        opsForHash.put(redisKey, String.valueOf(product.getId()), gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String redisValue = opsForHash.get(redisKey, String.valueOf(productId));

        if (StringUtils.isEmpty(redisKey)) {
            return ResponseVo.error(CART_PRODUCT_NOT_EXIST);
        }

        Cart cart = gson.fromJson(redisValue, Cart.class);
        if (cartUpdateForm.getQuantity() != null && cartUpdateForm.getQuantity() >= 0) {
            cart.setQuantity(cartUpdateForm.getQuantity());
        }
        if (cartUpdateForm.getSelected() != null) {
            cart.setProductSelected(cartUpdateForm.getSelected());
        }
        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        if (StringUtils.isEmpty(redisKey)) {
            return ResponseVo.error(CART_PRODUCT_NOT_EXIST);
        }

        opsForHash.delete(redisKey, String.valueOf(productId));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey, String.valueOf(cart.getProductId()), gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey, String.valueOf(cart.getProductId()), gson.toJson(cart));
        }

        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum);
        return ResponseVo.success(sum);
    }

    @Override
    public List<Cart> listForCart(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        List<Cart> carts = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            carts.add(gson.fromJson(entry.getValue(), Cart.class));
        }

        return carts;
    }
}
