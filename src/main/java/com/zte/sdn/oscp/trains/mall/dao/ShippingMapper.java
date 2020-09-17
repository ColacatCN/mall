package com.zte.sdn.oscp.trains.mall.dao;

import com.zte.sdn.oscp.trains.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ShippingMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUidAndId(@Param("uid") Integer uid, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByUserId(@Param("uid") Integer uid);

    Shipping selectByUserIdAndShippingId(@Param("uid") Integer uid, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByIdSet(@Param("idSet") Set<Integer> idSet);
}
