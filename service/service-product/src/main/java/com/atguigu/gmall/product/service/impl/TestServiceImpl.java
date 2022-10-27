package com.atguigu.gmall.product.service.impl;

import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

   @Autowired
   private StringRedisTemplate redisTemplate;

   // 执行删除原子行
   @Override
   public void testLock() {
      //  设置uuId
      String uuid = UUID.randomUUID().toString();
      //  缓存的lock 对应的值 ，应该是index2 的uuid
      Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", uuid,1, TimeUnit.SECONDS);
      //  判断flag index=1
      if (flag){
         //  说明上锁成功！ 执行业务逻辑
         String value = redisTemplate.opsForValue().get("num");
         //  判断
         if(StringUtils.isEmpty(value)){
            return;
         }
         //  进行数据转换
         int num = Integer.parseInt(value);
         //  放入缓存
         redisTemplate.opsForValue().set("num",String.valueOf(++num));

         //  定义一个lua 脚本
         String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

         //  准备执行lua 脚本
         DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
         //  将lua脚本放入DefaultRedisScript 对象中
         redisScript.setScriptText(script);
         //  设置DefaultRedisScript 这个对象的泛型
         redisScript.setResultType(Long.class);
         //  执行删除
         redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);

      }else {
         //  没有获取到锁！
         try {
            Thread.sleep(1000);
            //  睡醒了之后，重试
            testLock();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }


   /*@Override
   public void testLock() {
      // 1. 从redis中获取锁,setnx
      Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", "111");
      if (lock) {
         // 查询redis中的num值
         String value = (String)this.redisTemplate.opsForValue().get("num");
         // 没有该值return
         if (StringUtils.isBlank(value)){
            return ;
         }
         // 有值就转成成int
         int num = Integer.parseInt(value);
         // 把redis中的num值+1
         this.redisTemplate.opsForValue().set("num", String.valueOf(++num));

         // 2. 释放锁 del
         this.redisTemplate.delete("lock");
      } else {
         // 3. 每隔1秒钟回调一次，再次尝试获取锁
         try {
            Thread.sleep(100);
            testLock();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }*/


/*   @Override
   public synchronized void testLock() {
      // 查询redis中的num值
      String value = (String)this.redisTemplate.opsForValue().get("num");
      // 没有该值return
      if (StringUtils.isBlank(value)){
         return ;
      }
      // 有值就转成成int
      int num = Integer.parseInt(value);
      // 把redis中的num值+1
      this.redisTemplate.opsForValue().set("num", String.valueOf(++num));
   }*/
}
