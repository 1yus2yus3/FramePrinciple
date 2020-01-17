package com.coal.futuretask.myself;

import sun.misc.Unsafe;

import java.util.concurrent.*;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2020/01/14 11:16
 * @description: 自定义FutureTask
 */
public class CoalFutureTask<R> implements Runnable,Future<R> {

    /**
     * 封装执行业务的方法参数
     */
    Callable<R> callable;
    //业务执行返回的数据
    R result;

    /***
     * 构造方法实例化callable
     */
    public CoalFutureTask(Callable<R> callable) {
        this.callable = callable;
    }

    public void run() {
        try {
            result = callable.call();
            //=====> 当代码执行到此处，一定是执行方法完成，至此唤醒所有线程
            synchronized (this) {
                this.notifyAll();
            }
        }catch (Exception e) {
            //todo
        }
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public R get() throws InterruptedException, ExecutionException {
        //如果当前结果已经返回，直接返回
        if(result != null) {
            return result;
        }
        synchronized (this) {
            //当前线程等待，直到被唤醒
            this.wait();
        }
        return result;
    }

    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
