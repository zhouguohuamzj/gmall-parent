package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    void addToCart(String userId, Long skuId, Integer skuNum);

    List<CartInfo> getCartList(String userId, String userTempId);

    /**
     * @param userId
     * @param isChecked
     * @param skuId
     */
    void checkCart(String userId, Integer isChecked, Long skuId);

    void deleteCart(Long skuId, String userId);

}
