package juc.c_000;

import java.util.concurrent.TimeUnit;

/**
 * 线程的调用方式
 * @author wangwren
 */
public class T01_WhatIsThread {
    private static class T1 extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < 10; i ++){
                try {
                    //睡1微秒
                    TimeUnit.MICROSECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(i);
            }
        }
    }

    public static void main(String[] args) {
        T1 t = new T1();

        //调用线程
        t.start();

        try {
            //主线程睡1微秒
            TimeUnit.MICROSECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("main");
    }
}
