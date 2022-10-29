package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @description:
 * @title: ItemServiceImpl
 * @Author coderZGH
 * @Date: 2022/10/25 14:41
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;


    @Autowired
    private ThreadPoolExecutor executor;


    /**
     * 用于远程调用service-product获取数据，进行汇总
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemById(Long skuId) {
        //  声明对象
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                //  获取到的数据是skuInfo + skuImageList
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                result.put("skuInfo", skuInfo);
                return skuInfo;
            }
        }, executor);

        // 销售属性-销售属性值回显并锁定
        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //  获取销售属性+销售属性值
                List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
                result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
            }
        }, executor);

        CompletableFuture<Void> skuValueIdsMapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            //  查询销售属性值Id 与skuId 组合的map
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            //  将这个map 转换为页面需要的Json 对象
            String valueJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valueJson);
        }), executor);

        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            //  获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        }), executor);

        //获取商品最新价格
        CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price", skuPrice);
        }, executor);


        //  获取海报数据
        CompletableFuture<Void> spuPosterListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            //  spu海报数据
            List<SpuPoster> spuPosterList = productFeignClient.getSpuPosterBySpuId(skuInfo.getSpuId());
            result.put("spuPosterList", spuPosterList);
        }, executor);

        //  获取sku平台属性，即规格数据
        CompletableFuture<Void> skuAttrListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
            //  使用拉姆达表示
            List<Map<String, String>> skuAttrList = attrList.stream().map((baseAttrInfo) -> {
                Map<String, String> attrMap = new HashMap<>();
                attrMap.put("attrName", baseAttrInfo.getAttrName());
                attrMap.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
                return attrMap;
            }).collect(Collectors.toList());
            result.put("skuAttrList", skuAttrList);
        }, executor);


        CompletableFuture.allOf(skuInfoCompletableFuture,
                spuSaleAttrCompletableFuture,
                skuValueIdsMapCompletableFuture,
                skuPriceCompletableFuture, categoryViewCompletableFuture,
                spuPosterListCompletableFuture, skuAttrListCompletableFuture).join();


        return result;
    }

}
