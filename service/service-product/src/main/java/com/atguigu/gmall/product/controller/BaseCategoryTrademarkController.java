package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @title: BaseCategoryTrademark
 * @Author coderZGH
 * @Date: 2022/10/24 7:30
 * @Version 1.0
 */
@Api(tags = "分类品牌管理")
@RestController
@RequestMapping("admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {

    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;

    @Autowired
    private BaseTrademarkService baseTrademarkService;


    // /admin/product/baseCategoryTrademark/findCurrentTrademarkList/{category3Id}
    @ApiOperation("查询三级分类下的品牌列表")
    @GetMapping("findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable Long category3Id) {
        // 通过中间件查询该分类下的所有品牌id集合
        QueryWrapper<BaseCategoryTrademark> categoryTrademarkQueryWrapper = new QueryWrapper<>();
        categoryTrademarkQueryWrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> categoryTrademarks = baseCategoryTrademarkService.list(categoryTrademarkQueryWrapper);

        // 返回的数据
        List<BaseTrademark> trademarkList = null;

        if (!CollectionUtils.isEmpty(categoryTrademarks)) {

            List<Long> trademarkIds = categoryTrademarks.stream().map(categoryTrademark -> {
                return categoryTrademark.getTrademarkId();
            }).collect(Collectors.toList());

            // 查询trademarkIds的所有对像
            QueryWrapper<BaseTrademark> trademarkQueryWrapper = new QueryWrapper<>();
            trademarkQueryWrapper.in("id", trademarkIds);
            trademarkList = baseTrademarkService.list(trademarkQueryWrapper);
        }
        return Result.ok(trademarkList);
    }

    @ApiOperation("获取该分类下可选的品牌列表")
    @GetMapping("findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList(@PathVariable Long category3Id) {
        List<BaseTrademark> data = baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);
        return Result.ok(data);
    }

    @ApiOperation("保存该分类下的品牌列表关系")
    @PostMapping("save")
    public Result save(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.save(categoryTrademarkVo);
        return Result.ok();
    }

    @ApiOperation("删除该分类的品牌关系")
    @DeleteMapping("remove/{category3Id}/{trademarkId}")
    public Result remove(@PathVariable Long category3Id,
                         @PathVariable Long trademarkId){

        baseCategoryTrademarkService.remove(category3Id, trademarkId);
        return Result.ok();
    }

}