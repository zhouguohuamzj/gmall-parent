package com.atguigu.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.IPUtil;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @title: PassportApiController
 * @Author coderZGH
 * @Date: 2022/11/11 1:32
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        UserInfo user = userInfoService.login(userInfo);

        if (user != null) {
            // 生成token
            String token = UUID.randomUUID().toString().replace("-", "");

            // 放入redis中的key
            String key = RedisConst.USER_LOGIN_KEY_PREFIX + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user.getId().toString());
            jsonObject.put("ip", IpUtil.getIpAddress(request));
            redisTemplate.opsForValue().set(key, jsonObject.toJSONString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);

            // 将token和nickName返回给浏览器
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("nickName", user.getNickName());

            return Result.ok(map);
        } else {
            return Result.fail().message("用户名和密码错误");
        }
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("logout")
    public Result logout(HttpServletRequest request){
        redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + request.getHeader("token"));
        return Result.ok();
    }

}