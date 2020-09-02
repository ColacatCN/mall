package com.zte.sdn.oscp.trains.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zte.sdn.oscp.trains.mall.dao.OrderItemMapper;
import com.zte.sdn.oscp.trains.mall.dao.OrderMapper;
import com.zte.sdn.oscp.trains.mall.dao.ProductMapper;
import com.zte.sdn.oscp.trains.mall.dao.ShippingMapper;
import com.zte.sdn.oscp.trains.mall.pojo.Cart;
import com.zte.sdn.oscp.trains.mall.pojo.Order;
import com.zte.sdn.oscp.trains.mall.pojo.OrderItem;
import com.zte.sdn.oscp.trains.mall.pojo.Product;
import com.zte.sdn.oscp.trains.mall.pojo.Shipping;
import com.zte.sdn.oscp.trains.mall.service.ICartService;
import com.zte.sdn.oscp.trains.mall.service.IOrderService;
import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.zte.sdn.oscp.trains.mall.enums.OrderStatusEnum.NO_PAY;
import static com.zte.sdn.oscp.trains.mall.enums.PaymentTypeEnum.PAY_ONLINE;
import static com.zte.sdn.oscp.trains.mall.enums.ProductStatusEnum.ON_SELL;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.CART_SELECTED_IS_EMPTY;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_NOT_EXIST;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_OFF_SELL_OR_DELETE;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.PRODUCT_STOCK_ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.SHIPPING_NOT_EXISTS;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ICartService cartService;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Transactional
    @Override
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        // 1. 校验收货地址
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(uid, shippingId);
        if (shipping == null) {
            return ResponseVo.error(SHIPPING_NOT_EXISTS);
        }

        // 2. 获取购物车
        List<Cart> cartList = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(cartList)) {
            return ResponseVo.error(CART_SELECTED_IS_EMPTY);
        }

        List<Integer> productIdList = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());
        Map<Integer, Product> productMap = productMapper.selectByProductIdList(productIdList).stream().
                collect(Collectors.toMap(Product::getId, product -> product));

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();
        for (Cart cart : cartList) {
            Product product = productMap.get(cart.getProductId());
            if (product == null) {
                return ResponseVo.error(PRODUCT_NOT_EXIST);
            }
            if (!ON_SELL.getCode().equals(product.getStatus())) {
                return ResponseVo.error(PRODUCT_OFF_SELL_OR_DELETE);
            }
            if (product.getStock() < cart.getQuantity()) {
                return ResponseVo.error(PRODUCT_STOCK_ERROR);
            }
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);
        }

        // 3. 计算被选中商品的总价格`
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);

        // 4. 生成订单，入库 order 和 order_item（ 事务 ）
        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0) {
            return ResponseVo.error(ERROR);
        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0) {
            return ResponseVo.error(ERROR);
        }

        // 5. 减库存

        // 6. 更新购物车

        return null;
    }

    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(uid);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

    private Order buildOrder(Integer uid, Long orderNo, Integer shippingId, List<OrderItem> orderItemList) {
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setUserId(uid);
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(NO_PAY.getCode());
        return order;
    }
}
