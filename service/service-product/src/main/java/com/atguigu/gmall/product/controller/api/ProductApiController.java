package com.atguigu.gmall.product.controller.api;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.google.j2objc.annotations.AutoreleasePool;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @title: ProductApiController
 * @Author coderZGH
 * @Date: 2022/10/25 14:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/product/inner")
public class ProductApiController {

    @Autowired
    private ManagerService managerService;


    @ApiOperation("根据skuId获取SkuInfo与图片信息")
    @GetMapping("getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        SkuInfo skuInfo = managerService.getSkuInfo(skuId);
        return skuInfo;
    }

    @ApiOperation("根据skuId获取SkuInfo与图片信息")
    @GetMapping("getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId) {
        return managerService.getSkuPriceById(skuId);
    }

    @ApiOperation("根据spuId 获取海报数据")
    @GetMapping("findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId) {
        return managerService.findSpuPosterBySpuId(spuId);
    }

    @ApiOperation("根据三级分类id查询分类信息")
    @GetMapping("getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewById(@PathVariable Long category3Id) {
        return managerService.getCategoryViewById(category3Id);
    }


    @ApiOperation("根据spuId,skuId 获取销售属性数据,标志已选中的值")
    @GetMapping("getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                    @PathVariable Long spuId) {

        return managerService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @ApiOperation("根据spuId 获取到销售属性值Id 与skuId 组成的数据集")
    @GetMapping("getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId) {
        return managerService.getSkuValueIdsMap(spuId);
    }


    @ApiOperation("根据skuId 获取平台属性数据")
    @GetMapping("getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId){

        return managerService.getAttrListBySkuId(skuId);
    }
}