package com.atguigu.gmall.list.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.stereotype.Component;

@Component
public class ListDegradeFeignClient implements ListFeignClient {

    /**
     * 搜索商品
     *
     * @param searchParam
     * @return
     * @throws
     */
    @Override
    public Result list(SearchParam searchParam) {

        return null;
    }

    @Override
public Result incrHotScore(Long skuId) {
    return null;
}

    /**
     * 上架商品
     *
     * @param skuId
     * @return
     */
    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    /**
     * 下架商品
     *
     * @param skuId
     * @return
     */
    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }
}
