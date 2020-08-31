package com.zte.sdn.oscp.trains.mall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cart {

    private Integer productId;

    private Integer quantity;

    private Boolean productSelected;
}
