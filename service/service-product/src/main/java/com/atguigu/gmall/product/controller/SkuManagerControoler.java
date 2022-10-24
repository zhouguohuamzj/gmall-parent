package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.message.ReusableMessage;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @title: SkuManagerControoler
 * @Author coderZGH
 * @Date: 2022/10/24 17:53
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class SkuManagerControoler {

    @Autowired
    private ManagerService managerService;

    @ApiOperation("sku分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable Long page ,
                       @PathVariable Long limit){

        Page<SkuInfo> infoPage = new Page<>(page, limit);
        IPage<SkuInfo> result = managerService.list(infoPage);
        return Result.ok(result);
    }

    @ApiOperation("获取该SPU下的所有销售属性")
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId){
      List<SpuSaleAttr> data = managerService.getSpuSaleAttrList(spuId);
        return Result.ok(data);
    }

    @ApiOperation("根据spuId获取所有的图片列表")
    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId) {
        List<SpuImage> data = managerService.getSpuImageList(spuId);
        return Result.ok(data);
    }

    /**
     * 保存sku
     * @param skuInfo
     * @return
     */
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        // 调用服务层
        managerService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

}