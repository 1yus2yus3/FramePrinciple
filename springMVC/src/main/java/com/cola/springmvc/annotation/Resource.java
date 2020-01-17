package com.cola.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2019/03/17 12:41
 * @description:
 */
@Target(ElementType.FIELD) //作用范围：Autowire作用于类的属性上
@Retention(RetentionPolicy.RUNTIME) //系统运行时，通过反射获取信息
@Documented
public @interface Resource {
    String value() default "";
}
