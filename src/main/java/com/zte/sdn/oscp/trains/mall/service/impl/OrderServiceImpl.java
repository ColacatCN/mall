package com.zte.sdn.oscp.trains.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import com.zte.sdn.oscp.trains.mall.vo.OrderItemVo;
import com.zte.sdn.oscp.trains.mall.vo.OrderVo;
import com.zte.sdn.oscp.trains.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zte.sdn.oscp.trains.mall.enums.OrderStatusEnum.CANCELED;
import static com.zte.sdn.oscp.trains.mall.enums.OrderStatusEnum.NO_PAY;
import static com.zte.sdn.oscp.trains.mall.enums.OrderStatusEnum.PAYED;
import static com.zte.sdn.oscp.trains.mall.enums.PaymentTypeEnum.PAY_ONLINE;
import static com.zte.sdn.oscp.trains.mall.enums.ProductStatusEnum.ON_SELL;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.CART_SELECTED_IS_EMPTY;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ERROR;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ORDER_NOT_EXISTS;
import static com.zte.sdn.oscp.trains.mall.enums.ResponseEnum.ORDER_STATUS_EXISTS;
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

            // 3. 减库存
            product.setStock(product.getStock() - cart.getQuantity());
            int rowForProduct = productMapper.updateByPrimaryKeySelective(product);
            if (rowForProduct <= 0) {
                return ResponseVo.error(ERROR);
            }
        }

        // 4. 计算被选中商品的总价格`
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);

        // 5. 生成订单，入库 order 和 order_item（ 事务 ）
        int rowForOrder = orderMapper.insertSelective(order);
        if (rowForOrder <= 0) {
            return ResponseVo.error(ERROR);
        }
        int rowForOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowForOrderItem <= 0) {
            return ResponseVo.error(ERROR);
        }

        // 6. 更新购物车
        for (Cart cart : cartList) {
            cartService.delete(uid, cart.getProductId());
        }

        // 7. 返回 OrderVo
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
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

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        List<OrderItemVo> orderItemVoList = orderItemList.stream()
                .map(orderItem -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    BeanUtils.copyProperties(orderItem, orderItemVo);
                    return orderItemVo;
                }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);
        if (shipping != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = orderMapper.selectByUserId(uid);

        Set<Long> orderNoSet = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));

        Set<Integer> shippingIdSet = orderList.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order, orderItemMap.get(order.getOrderNo()), shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ORDER_NOT_EXISTS);
        }
        Set<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ORDER_NOT_EXISTS);
        }

        if (!NO_PAY.getCode().equals(order.getStatus())) {
            return ResponseVo.error(ORDER_STATUS_EXISTS);
        }

        order.setStatus(CANCELED.getCode());
        order.setCloseTime(new Date());
        int rowForOrder = orderMapper.updateByPrimaryKeySelective(order);
        if (rowForOrder <= 0) {
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException(ORDER_NOT_EXISTS.getDesc() + " " + orderNo);
        }

        if (!NO_PAY.getCode().equals(order.getStatus())) {
            throw new RuntimeException(ORDER_STATUS_EXISTS.getDesc() + " " + orderNo);
        }

        order.setStatus(PAYED.getCode());
        order.setPaymentTime(new Date());
        int rowForOrder = orderMapper.updateByPrimaryKeySelective(order);
        if (rowForOrder <= 0) {
            throw new RuntimeException(ERROR.getDesc());
        }
    }
}
