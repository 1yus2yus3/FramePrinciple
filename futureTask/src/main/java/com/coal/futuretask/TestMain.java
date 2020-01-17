package com.coal.futuretask;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2020/01/14 14:27
 * @description:
 */
public class TestMain {

    public static void main(String[] args)  throws Exception{
        Field declaredField = Unsafe.class.getDeclaredField("theUnsafe");
        declaredField.setAccessible(true);
        Unsafe unsafe = (Unsafe)declaredField.get(null);
        unsafe.allocateInstance(Integer.class);
    }
}
