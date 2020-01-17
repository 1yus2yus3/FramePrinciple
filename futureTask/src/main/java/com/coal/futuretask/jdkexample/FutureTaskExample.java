package com.coal.futuretask.jdkexample;

import com.coal.futuretask.myself.CoalFutureTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2020/01/10 13:49
 * @description:
 */
public class FutureTaskExample {

    /**
     * jdk创建一个线程池 ! 手动创建线程池容易导致多出创建
     */
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        futureTaskDemo();
    }


    public static void futureTaskDemo(){

        Long startTime = System.currentTimeMillis();
        //1==> 定义Callable
        Callable<Map> queryUrl1 = new Callable<Map>() {
            public Map call() throws Exception {
                // todo something
                Thread.sleep(1000L);
                System.out.println("执行线程：" + Thread.currentThread().getName());
                return new HashMap();
            }
        };

        Callable<Map> queryUrl2 = new Callable<Map>() {
            public Map call() throws Exception {
                // todo something
                Thread.sleep(3000L);
                System.out.println("执行线程：" + Thread.currentThread().getName());
                return new HashMap();
            }
        };
        //2==> 定义futureTask
        FutureTask<Map> futureTask1 = new FutureTask<Map>(queryUrl1);
        FutureTask<Map> futureTask2 = new FutureTask<Map>(queryUrl2);

        /** 使用自己定义的 CoalFutureTask
        //2==> 定义futureTask
        CoalFutureTask<Map> futureTask1 = new CoalFutureTask<Map>(queryUrl1);
        CoalFutureTask<Map> futureTask2 = new CoalFutureTask<Map>(queryUrl2);
         **/

        //3==> 启动多线程执行方法 每一个线程独立执行，主线程直接进入下一步，不会等到其他线程
        executorService.submit(futureTask1);
        executorService.submit(futureTask2);


        //4：获取子线程中的回调数据
        try {
            //单独的线程会等待
            Map resultMap1 = futureTask1.get();
            Long endTime1 = System.currentTimeMillis();
            System.out.println("耗时："+(endTime1 - startTime) + "ms");
            Map resultMap2 = futureTask2.get();
            Long endTime2 = System.currentTimeMillis();
            System.out.println("耗时："+(endTime2 - startTime) + "ms");
        }catch (Exception e){
        }

        //5：关闭线程池，不然主线程不会终止
        executorService.shutdown();
    }

}
