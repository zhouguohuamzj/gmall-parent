package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description:
 * @title: CartApiController
 * @Author coderZGH
 * @Date: 2022/11/15 1:35
 * @Version 1.0
 */
@RestController
@RequestMapping("api/cart/")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @ApiOperation("展示购物车")
    @GetMapping("cartList")
    public Result cartList(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
      List<CartInfo> data =  cartService.getCartList(userId,userTempId);
        return Result.ok(data);
    }

    @ApiOperation("添加到购物车")
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum,
                            HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }

        cartService.addToCart(userId, skuId, skuNum);

        return Result.ok();
    }

    //  选中状态
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked,
                            HttpServletRequest request){

        String userId = AuthContextHolder.getUserId(request);
        //  判断
        if (StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        //  调用服务层方法
        cartService.checkCart(userId,isChecked,skuId);
        return Result.ok();
    }

    /**
     * 删除
     *
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request) {
        // 如何获取userId
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            // 获取临时用户Id
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.deleteCart(skuId, userId);
        return Result.ok();
    }


}