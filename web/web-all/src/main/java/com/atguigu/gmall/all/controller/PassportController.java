package com.atguigu.gmall.all.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @title: PassportController
 * @Author coderZGH
 * @Date: 2022/11/11 1:26
 * @Version 1.0
 */
@Controller
public class PassportController {


    @ApiOperation("跳转登录页面")
    @GetMapping("login.html")
    public String login(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl", originUrl);
        return "login";
    }
}