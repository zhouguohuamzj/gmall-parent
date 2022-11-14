package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BaseTrademarkService extends IService<BaseTrademark> {

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    BaseTrademark getTrademarkByTmId(Long tmId);
}
