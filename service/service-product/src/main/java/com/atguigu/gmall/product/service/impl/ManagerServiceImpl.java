package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    /**
     * 分页查询SPuInfo
     *
     * @return
     * @param infoPage
     * @param category3Id
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

        if (!CollectionUtils.isEmpty(values)){
             attrInfo.setAttrValueList(values);
             return attrInfo;
        }

        return attrInfo;
    }

    /**
     * 根据属性id获取属性值集合
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