package com.cola.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2019/03/17 12:41
 * @description:
 */
@Target(ElementType.TYPE) //作用范围：Controller属于类上的注解
@Retention(RetentionPolicy.RUNTIME) //系统运行时，通过反射获取信息
@Documented
public @interface Controller {
    String value() default "";
}
