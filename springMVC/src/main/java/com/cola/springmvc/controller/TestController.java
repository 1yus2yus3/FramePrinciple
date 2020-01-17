package com.cola.springmvc.controller;

import com.cola.springmvc.annotation.Resource;
import com.cola.springmvc.annotation.Controller;
import com.cola.springmvc.annotation.RequestMapping;
import com.cola.springmvc.annotation.RequstParam;
import com.cola.springmvc.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2019/03/17 14:11
 * @description:
 */

@Controller("TestController")
@RequestMapping("/test")
public class TestController {


    @Resource("TestService")
    TestService testService;

    @RequestMapping(value = "/hello")
    public void test(
            HttpServletRequest request, HttpServletResponse response,
                     @RequstParam("name") String name){

        try {

            //PrintWriter writer = response.getWriter();
            String re = testService.test(name);
            //writer.print(re);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
