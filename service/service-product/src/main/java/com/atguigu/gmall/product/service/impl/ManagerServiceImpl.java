package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取分类数据
     *
     * @return
     */
    @Override
    public List<JSONObject> getBaseCategoryList() {
        ArrayList<JSONObject> list = new ArrayList<>();

        List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        int index = 1;
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : category1Map.entrySet()) {
            // 获取一级分类id
            Long category1Id = entry1.getKey();
            // 获取一级分类下的所有集合
            List<BaseCategoryView> category2List = entry1.getValue();
            JSONObject category1 = new JSONObject();
            category1.put("index", index);
            category1.put("categoryId", category1Id);
            category1.put("categoryName", category2List.get(0).getCategory1Name());
            index++;

            // 循环获取二级分类数据
            Map<Long, List<BaseCategoryView>> category2Map  = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            // 声明二级分类对象集合
            List<JSONObject> category2Child = new ArrayList<>();
            // 循环遍历
            for (Map.Entry<Long, List<BaseCategoryView>> entry2  : category2Map.entrySet()) {
                // 获取二级分类Id
                Long category2Id  = entry2.getKey();
                // 获取二级分类下的所有集合
                List<BaseCategoryView> category3List  = entry2.getValue();
                // 声明二级分类对象
                JSONObject category2 = new JSONObject();

                category2.put("categoryId",category2Id);
                category2.put("categoryName",category3List.get(0).getCategory2Name());
                // 添加到二级分类集合
                category2Child.add(category2);

                List<JSONObject> category3Child = new ArrayList<>();

                // 循环三级分类数据
                category3List.stream().forEach(category3View -> {
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId",category3View.getCategory3Id());
                    category3.put("categoryName",category3View.getCategory3Name());

                    category3Child.add(category3);
                });

                // 将三级数据放入二级里面
                category2.put("categoryChild",category3Child);

            }
            // 将二级数据放入一级里面
            category1.put("categoryChild",category2Child);
            list.add(category1);
        }
        return list;

    }

    /**
     * 根据skuId 获取平台属性数据
     *
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "BaseAttrInfoList:")
    public List<BaseAttrInfo> getAttrListBySkuId(Long skuId) {
        return baseAttrInfoMapper.getAttrListBySkuId(skuId);
    }

    /**
     * 根据spuId 获取到销售属性值Id 与skuId 组成的数据集
     *
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "saleAttrValuesBySpu:")
    public Map getSkuValueIdsMap(Long spuId) {
        List<Map> mapList = skuSaleAttrValueMapper.getSkuValueIdsMap(spuId);
        Map<Object, Object> map = new HashMap<>();
        if (mapList != null && mapList.size() > 0) {
            // 循环遍历
            for (Map skuMap : mapList) {
                // key = 125|123 ,value = 37
                map.put(skuMap.get("value_ids"), skuMap.get("sku_id"));
            }
        }
        return map;

    }

    /**
     * 根据spuId,skuId 获取销售属性数据,标志已选中的值
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "spuSaleAttrListCheckBySku:")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    /**
     * 根据三级分类id查询分类信息
     *
     * @param category3Id
     * @return
     */
    @Override
    @GmallCache(prefix = "categoryViewByCategory3Id:")
    public BaseCategoryView getCategoryViewById(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 根据spuId 获取海报数据
     *
     * @param spuId
     * @return
     */
    @Override
    @GmallCache(prefix = "SpuPosterList:")
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        QueryWrapper<SpuPoster> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(wrapper);
        return spuPosterList;
    }

    /**
     * 根据skuId获取SkuInfo与价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPriceById(Long skuId) {
        RLock lock = redissonClient.getLock(skuId + ":lock");
        lock.lock();

        SkuInfo skuInfo = null;
        BigDecimal price = new BigDecimal(0);


        try {
             skuInfo = skuInfoMapper.selectById(skuId);
            if (skuInfo != null) {
                price =  skuInfo.getPrice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return price;
    }

    /**
     * 根据skuId获取SkuInfo与图片信息
     *
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX)
    public SkuInfo getSkuInfo(Long skuId) {

        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoRedisson(Long skuId) {
        SkuInfo skuInfo = null;
        try {
            // 定义存储数据的key
            String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            // 从缓存中获取数据
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);

            // 判断从缓存中获取的数据
            if (skuInfo == null) {
                // 缓存中不存在数据，需要查询数据。防止出现缓存击穿，上锁
                // 定义锁的key
                String lockKey = RedisConst.SKUKEY_PREFIX + skuKey + RedisConst.SKULOCK_SUFFIX;
                RLock lock = redissonClient.getLock(lockKey);
                boolean flag = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (flag) {
                    try {
                        // 上锁成功
                        // 从数据库中获取数据
                        skuInfo = getSkuInfoDB(skuId);
                        // 判断数据是否空
                        if (skuInfo == null) {
                            // 避免发生缓存穿透，将空对象放入缓存
                            SkuInfo skuInfo1 = new SkuInfo();
                            redisTemplate.opsForValue().set(skuKey, skuInfo1);
                            return skuInfo1;
                        } else {
                            // 数据库中有值，存入缓存
                            redisTemplate.opsForValue().set(skuKey, skuInfo);
                            return skuInfo;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }else {
                    // 其他线程等待，自旋
                    // 其他线程等待
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            } else {
                // 不为空，返回数据
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 为了防止缓存宕机，查询数据库
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoRedis(Long skuId) {
        SkuInfo skuInfo = null;
        try {
            // 先从缓存中获取数据
            String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);

            // 判断从缓存中的数据是否为空
            if (skuInfo == null) {
                // 直接从数据库取数据可能会造成缓存击穿
                // 上锁
                String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                // 定义锁的值
                String uuid = UUID.randomUUID().toString().replace("-", "");
                Boolean luck = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (luck) {
                    // 上锁成功， 从数据库里面读取数据
                    skuInfo = getSkuInfoDB(skuId);
                    if (skuInfo == null) {
                        // 避免缓存穿透， 将空对象放入缓存
                        SkuInfo skuInfo1 = new SkuInfo();
                        //对象的地址
                        redisTemplate.opsForValue().set(skuKey, skuInfo1, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        return skuInfo1;
                    }
                    // 查询数据库，有值放入数据库
                    redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    // 解锁：使用lua 脚本解锁
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 设置lua脚本返回的数据类型
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    // 设置lua脚本返回类型为Long
                    redisScript.setResultType(Long.class);
                    redisScript.setScriptText(script);
                    // 删除key 所对应的 value
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey), uuid);
                    return skuInfo;
                } else {
                    // 其他线程等待
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            } else {
                // 缓存中有值
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 为了防止缓存宕机：从数据库中获取数据
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoDB(Long skuId) {
        // 获取skuInfo基本信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        // 获取skuInfo图片信息
        QueryWrapper<SkuImage> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(imageQueryWrapper);
        if (skuInfo != null) {
            skuInfo.setSkuImageList(skuImageList);

        }
        return skuInfo;
    }

    /**
     * 商品的下架
     *
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 商品的上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

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