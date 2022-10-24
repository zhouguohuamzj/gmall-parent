package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @title: BaseCategoryTrademarkServiceImpl
 * @Author coderZGH
 * @Date: 2022/10/24 7:33
 * @Version 1.0
 */
@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {

    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    /**
     * 获取该分类下可选的品牌列表
     *
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {
        // 查询该分类下已关联的品牌
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> selectedList = baseCategoryTrademarkMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(selectedList)) {
            // 得到所有的已选择的id
            List<Long> trademarkIds = selectedList.stream().map(categoryTrademark -> {
                return categoryTrademark.getTrademarkId();
            }).collect(Collectors.toList());

            // 查询所有的品牌列表
            // 进行过滤，得到可选的品牌列表
            List<BaseTrademark> trademarkList = baseTrademarkMapper.selectList(null).stream().filter(tradmark -> {
                return !trademarkIds.contains(tradmark.getId());
            }).collect(Collectors.toList());
            return trademarkList;
        } else {
            // 为空，说明该分类下未选择任何品牌
            return baseTrademarkMapper.selectList(null);
        }

    }

    /**
     * 保存分类和品牌的关系
     *
     * @param categoryTrademarkVo
     */
    @Override
    public void save(CategoryTrademarkVo categoryTrademarkVo) {
        // 先删除该分类下关联的关系
        // 多余操作
        /*QueryWrapper<BaseCategoryTrademark> delWrapper = new QueryWrapper<>();
        delWrapper.eq("category3_id", categoryTrademarkVo.getCategory3Id());
        baseCategoryTrademarkMapper.delete(delWrapper);*/

        // 在往关系表中新增数据
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        if (!CollectionUtils.isEmpty(trademarkIdList)){

            List<BaseCategoryTrademark> trademarkList = trademarkIdList.stream().map(id -> {
                BaseCategoryTrademark categoryTrademark = new BaseCategoryTrademark();
                categoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
                categoryTrademark.setTrademarkId(id);
                return categoryTrademark;
            }).collect(Collectors.toList());
            saveBatch(trademarkList);
        }
    }

    /**
     * 删除分类和品牌的关系
     *
     * @param category3Id
     * @param trademarkId
     */
    @Override
    public void remove(Long category3Id, Long trademarkId) {
        baseCategoryTrademarkMapper.delete(new QueryWrapper<BaseCategoryTrademark>().eq("category3_id", category3Id).eq("trademark_id", trademarkId));
    }
}