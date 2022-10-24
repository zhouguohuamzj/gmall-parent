package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zipkin2.Call;

/**
 * @description:
 * @title: BaseTrademarkServiceImpl
 * @Author coderZGH
 * @Date: 2022/10/24 7:07
 * @Version 1.0
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService  {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
}