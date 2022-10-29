package com.atguigu.gmall.product.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @description:
 * @title: CompletebaleFutureDemo
 * @Author coderZGH
 * @Date: 2022/10/29 5:59
 * @Version 1.0
 */
public class CompletebaleFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

/*        // 创建一个没有返回值的异步对象
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("没有返回值结果");
        });

        System.out.println(future.get());*/

        // 创建一个有返回值的异步对象
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = 1/0;
                return 404;
            }
        }).whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                System.out.println("whenComplete" + integer);
                System.out.println("whenComplete" + throwable);
            }
        });
        System.out.println(future1.get());
    }
}