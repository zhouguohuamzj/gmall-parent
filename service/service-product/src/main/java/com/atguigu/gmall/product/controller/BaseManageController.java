package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.extension.api.R;
import com.sun.org.apache.bcel.internal.generic.DRETURN;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @title: BaseManageController
 * @Author coderZGH
 * @Date: 2022/10/23 12:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
@Api(tags = "商品基础信息开发接口", value = "BaseManager")
public class BaseManageController {

    @Autowired
    private ManagerService managerService;



    //根据平台属性Id 获取到平台属性值集合
    @ApiOperation("根据平台属性id获取平台属性值集合")
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        BaseAttrInfo attrInfo =  managerService.getAttrInfo(attrId);
        List<BaseAttrValue> valueList = attrInfo.getAttrValueList();
        return Result.ok(valueList);
    }


    /**
     * 保存和修改属性值
     * @param baseAttrInfo
     * @return
     */
    @ApiOperation("保存和修改属性值")
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        managerService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 查询一级分类
     * @return
     */
    @ApiOperation("查询一级分类列表")
    @GetMapping("getCategory1")
    public Result getCategory1(){

        // 查询数据
        List<BaseCategory1> list =  managerService.getCategory1();
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }


    /**
     * 查询一级分类查询二级分类列表
     * @return
     */
    @ApiOperation("查询二级分类列表")
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){

        // 查询数据
        List<BaseCategory2> list =  managerService.getCategory2(category1Id);
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    /**
     * 根据分类的二级id查询三级分类列表
     * @param category2Id
     * @return
     */
    @ApiOperation("查询二级分类列表")
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id ){
       List<BaseCategory3> list =  managerService.getCategory3(category2Id);
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    /**
     * 根据分类id查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @ApiOperation("根据分类id查询平台属性列表")
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        List<BaseAttrInfo> list= managerService.attrInfoList(category1Id, category2Id, category3Id);
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }
}