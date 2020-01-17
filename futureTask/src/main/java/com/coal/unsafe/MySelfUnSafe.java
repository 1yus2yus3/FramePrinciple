package com.coal.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2020/01/14 15:01
 * @description:
 */
public class MySelfUnSafe {

    static Unsafe unsafe;

    /***
     * 名字的说明：Java官方不推荐使用Unsafe类，使用不好就会导致内存管理问题，所以取名不安全类
     * Unsafe ：内存管理类，可以像C语言一样直接操作内存地址，JAVA的CAS操作依赖该类实现
     * => 1：java内置的Unsafe类是只能JVM内部类调用，他会检测调用类所在的位置，不允许外部使用
     * => 2：外部如果想使用该类，可以通过反射获取到该类的unsafe属性，然后进行其他操作
     * => 3：unsafe.allocateInstance(Class<?> var1) 给指定对象分配内存空间，不会调用构造方法
     * => 4：unsafe.allocateMemory(size) 申请堆外内存，返回的是long型的地址，JVM不能管理该内存，需要手动freeMemory()
     * => 5：compareAndSwapObject(Object,offset,expected,x) 比较Object的offset处内存位置中的值和期望的值，如果相同则更新。此更新是不可中断的。
     * => 6：
     */


    public static void main(String[] args) throws Exception {
        /***
         *      => 1：java内置的Unsafe类是只能JVM内部类调用，他会检测调用类所在的位置，不允许外部使用
         */
        // 通过反射得到theUnsafe对应的Field对象
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        // 设置该Field为可访问
        field.setAccessible(true);
        // 通过Field得到该Field对应的具体对象，传入null是因为该Field为static的
        unsafe = (Unsafe) field.get(null);
        System.out.println(unsafe);

        /****
         *      => 4：unsafe.allocateMemory(size) 申请堆外内存，返回的是long型的地址，JVM不能管理该内存，需要手动freeMemory()
         */
        long data = 1000;
        //单位字节
        byte size = 1;
        //调用allocateMemory分配内存,并获取内存地址memoryAddress
        long memoryAddress = unsafe.allocateMemory(size);
        //直接往内存写入数据
        unsafe.putAddress(memoryAddress, data);
        //获取指定内存地址的数据
        long addrData=unsafe.getAddress(memoryAddress);
        System.out.println("addrData:"+addrData);
    }

    //=> 5: CAS的应用

    /**
     * 比较obj的offset处内存位置中的值和期望的值，如果相同则更新。此更新是不可中断的。
     *
     * @param obj 需要更新的对象
     * @param offset obj中整型field的偏移量
     * @param expect 希望field中存在的值
     * @param update 如果期望值expect与field的当前值相同，设置filed的值为这个新值
     * @return 如果field的值被更改返回true
     */
    public native boolean compareAndSwapInt(Object obj, long offset, int expect, int update);

    /***
     *
     * @param object 操作的对象
     * @param offset 对象对应的物理地址 物理地址在类初始化的时候就已经确定了，所以很多地方都是放在static里面进行获取的
     * @param var4 变化量
     * @return
     */
    public final int getAndAddInt(Object object, long offset, int var4) {
        int expect;
        do {
            //第一步执行：获取到最新的对象偏移量的值
            expect = unsafe.getIntVolatile(object, offset);
        }
        //第二步 执行体 也是终止条件 如果当前对象偏移量上的值在compareAndSwapInt方法执行的时候内部进行判断和excepet的值是否相同，
        // 如果相同说明在expect值之前是没有现成修改过，如果内部发现当前便宜量的值和expect不一致，说明该值已经被修改，需要自旋重新获取到最新的值，
        // 在执行变化量，所以通常 被修饰的值 用volitile修饰，保证其可见性，能读取到最新的值
        while(!this.compareAndSwapInt(object, offset, expect, expect + var4));

        return expect;
    }


}
