package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javafx.scene.control.cell.PropertyValueFactory;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @title: ManagerServiceImpl
 * @Author coderZGH
 * @Date: 2022/10/23 12:39
 * @Version 1.0
 */
@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private BaseCategory1Mapper category1Mapper;

    @Autowired
    private BaseCategory3Mapper category3Mapper;

    @Autowired
    private BaseCategory2Mapper category2Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;


    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private spuPosterMapper spuPosterMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
    /*
        skuInfo 库存单元表 --- spuInfo！
        skuImage 库存单元图片表 --- spuImage!
        skuSaleAttrValue sku销售属性值表{sku与销售属性值的中间表} --- skuInfo ，spuSaleAttrValue
        skuAttrValue sku与平台属性值的中间表 --- skuInfo ，baseAttrValue
     */
        skuInfoMapper.insert(skuInfo);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            // 循环遍历
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        // 调用判断集合方法
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    /**
     * Sku分页查询
     *
     * @param infoPage
     * @return
     */
    @Override
    public IPage<SkuInfo> list(Page<SkuInfo> infoPage) {
        IPage<SkuInfo> page = skuInfoMapper.selectPage(infoPage, new QueryWrapper<SkuInfo>().orderByAsc("id"));
        return page;
    }


    /**
     * 获取该SPU下的所有销售属性
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }


    /**
     * 根据spuId获取所有的图片列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(wrapper);
        return spuImageList;
    }


    /**
     * 获取所有的销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {

        QueryWrapper<BaseSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        return baseSaleAttrMapper.selectList(wrapper);
    }


    /**
     * 保存Spu相关信息
     *
     * @param spuInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
        // 保存spu_info基础信息
        spuInfoMapper.insert(spuInfo);

        // 保存spu_sale_attr，和销售属性中间表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                // 保存该spu下的一个属性
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                // 保存该属性下所有的属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }

            }
        }

        // 保存spu_image
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }

        //  获取到posterList 集合数据
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        //  判断不为空
        if (!CollectionUtils.isEmpty(spuPosterList)) {
            for (SpuPoster spuPoster : spuPosterList) {
                //  需要将spuId 赋值
                spuPoster.setSpuId(spuInfo.getId());
                //  保存spuPoster
                spuPosterMapper.insert(spuPoster);
            }


        }
    }

    /**
     * 分页查询SPuInfo
     *
     * @param infoPage
     * @param category3Id
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> infoPage, Long category3Id) {
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(infoPage, new QueryWrapper<SpuInfo>().eq("category3_id", category3Id));
        return spuInfoIPage;
    }

    /**
     * 根据平台属性id获取平台属性对象
     *
     * @param atrrId
     * @return
     */
    @Override
    public BaseAttrInfo getAttrInfo(Long atrrId) {
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectById(atrrId);

        List<BaseAttrValue> values = getAttrValueListByAttrId(atrrId);

        if (!CollectionUtils.isEmpty(values)) {
            attrInfo.setAttrValueList(values);
            return attrInfo;
        }

        return attrInfo;
    }

    /**
     * 根据属性id获取属性值集合
     *
     * @param atrrId
     * @return
     */
    private List<BaseAttrValue> getAttrValueListByAttrId(Long atrrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id", atrrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(wrapper);

        return baseAttrValues;
    }

    /**
     * 保存和修改平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 判断是新增还是修改操作
        if (baseAttrInfo.getId() != null) {
            // 进行修改操作
            baseAttrInfoMapper.updateById(baseAttrInfo);
            // 删除对应的属性值
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", baseAttrInfo.getId());
            baseAttrValueMapper.delete(wrapper);
        } else {
            // 进行新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // 保存平台属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }

    }

    /**
     * 查询一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> list = category1Mapper.selectList(null);
        return list;
    }

    /**
     * 根据一级分类id查询二级分类列表
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return category2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    /**
     * 根据二级分类id查询三级分类列表
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id", category2Id);

        List<BaseCategory3> baseCategory3s = category3Mapper.selectList(wrapper);

        return baseCategory3s;
    }

    /**
     * 根据分类id查询平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        // 调用mapper查询
        return baseAttrInfoMapper.selectAttrInfoList(category1Id, category2Id, category3Id);
    }
}