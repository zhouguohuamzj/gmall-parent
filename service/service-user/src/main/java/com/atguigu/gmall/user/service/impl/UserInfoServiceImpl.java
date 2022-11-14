package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @description:
 * @title: UserInfoServiceImpl
 * @Author coderZGH
 * @Date: 2022/11/11 1:37
 * @Version 1.0
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {

        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();

        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("login_name", loginName);
        wrapper.eq("passwd", newPasswd);

        UserInfo info = userInfoMapper.selectOne(wrapper);
        if (info != null) {
            return  info;
        }
        return null;
    }
}