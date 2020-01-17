package com.cola.springmvc.service;

import com.cola.springmvc.annotation.Service;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2019/03/17 14:12
 * @description:
 */
@Service("TestService")
public class TestServiceImpl implements TestService {

    @Override
    public String test(String name) {
        System.out.println("Hello World!"+name);

        return "Hello World!"+name;
    }
}
