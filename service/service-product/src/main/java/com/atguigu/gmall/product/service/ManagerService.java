package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ManagerService {
    /**
     * 查询一级分类
     * @return
     */
    List<BaseCategory1> getCategory1();

    /**
     * 根据一级分类id查询二级分类列表
     * @return
     * @param category1Id
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级分类id查询三级分类列表
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);


    /**
     * 根据分类id查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存和修改平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性id获取平台属性对象
     * @param atrrId
     * @return
     */
    BaseAttrInfo getAttrInfo(Long atrrId);

    /**
     * 分页查询SPuInfo
     * @return
     * @param infoPage
     * @param category3Id
     */
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> infoPage, Long category3Id);


    /**
     * 保存Spu相关信息
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 获取所有的销售属性
     * @return
     */
    List<BaseSaleAttr> baseSaleAttrList();

    /**
     * 根据spuId获取所有的图片列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 获取该SPU下的所有销售属性
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    /**
     * 保存sku相关信息
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * Sku分页查询
     * @param infoPage
     * @return
     */
    IPage<SkuInfo> list(Page<SkuInfo> infoPage);

    /**
     * 商品的上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 商品的下架
     * @param skuId
     */
    void cancelSale(Long skuId);


    /**
     * 根据skuId获取SkuInfo
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据skuId获取SkuInfo与图片信息
     * @param skuId
     * @return
     */
    BigDecimal getSkuPriceById(Long skuId);

    /**
     * 根据spuId 获取海报数据
     * @param spuId
     * @return
     */
    List<SpuPoster> findSpuPosterBySpuId(Long spuId);

    /**
     * 根据三级分类id查询分类信息
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryViewById(Long category3Id);

    /**
     * 根据spuId,skuId 获取销售属性数据,标志已选中的值
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     * 根据spuId 获取到销售属性值Id 与skuId 组成的数据集
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);

    /**
     * 根据skuId 获取平台属性数据
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrListBySkuId(Long skuId);

    /**
     * 获取分类数据
     * @return
     */
    List<JSONObject> getBaseCategoryList();


}
