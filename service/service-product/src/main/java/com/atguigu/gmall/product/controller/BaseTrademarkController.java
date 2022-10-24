package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @title: BaseTrademarkController
 * @Author coderZGH
 * @Date: 2022/10/24 7:02
 * @Version 1.0
 */
@Api("品牌管理")
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {


    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @ApiOperation("分页查询品牌列表")
    @GetMapping("{page}/{limit}")
    public Result getTrademarkPage(@PathVariable Long page,
                                   @PathVariable Long limit) {

        Page<BaseTrademark> pageInfo = new Page<>(page, limit);

        QueryWrapper<BaseTrademark> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");


        IPage<BaseTrademark> page1 = baseTrademarkService.page(pageInfo, queryWrapper);
        return Result.ok(page1);
    }

    @ApiOperation("保存品牌信息")
    @PostMapping("save")
    public Result saveTrademake(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("修改品牌信息")
    @PutMapping("update")
    public Result updateTradeMark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("获取品牌西欧模型")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    @ApiOperation("删除品牌信息")
    @DeleteMapping("remove/{id}")
    public Result updateTradeMark(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
}