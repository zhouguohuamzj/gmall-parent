package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @title: SpuManagerController
 * @Author coderZGH
 * @Date: 2022/10/24 6:49
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product")
public class SpuManagerController {

    @Autowired
    private ManagerService managerService;


    // /admin/product/{page}/{limit}

    @ApiOperation("分页查询SPU列表")
    @GetMapping("{page}/{limit}")
    public Result getSpuInfoPage(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 @RequestParam Long category3Id) {

        Page<SpuInfo> infoPage = new Page<>(page, limit);
        IPage<SpuInfo> spuInfoIPage = managerService.getSpuInfoPage(infoPage, category3Id);
        return Result.ok(spuInfoIPage);
    }

    @ApiOperation("保存SPU信息")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        managerService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @ApiOperation("获取基本销售属性列表")
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList() {
        List<BaseSaleAttr> data = managerService.baseSaleAttrList();
        return Result.ok(data);
    }

}