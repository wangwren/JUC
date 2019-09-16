package juc.c_017;

import java.util.concurrent.CountDownLatch;

/**
 * 门闩
 * @author: wangwren
 */
public class T01_TestCountDownLatch {

    public static void main(String[] args) {
        usingJoin();
        usingCountDownLatch();
    }

    /**
    * 使用CountDownLatch
    */
    public static void usingCountDownLatch(){
        Thread[] threads = new Thread[100];

        CountDownLatch latch = new CountDownLatch(threads.length);

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 1000; j++) {
                    result += j;
                }
                //latch - 1
                latch.countDown();
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        try {
            //在这等待，当latch减为0的时候，就不等待了，继续向下执行
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("end latch...");

    }

    /**
    * 使用join
    */
    public static void usingJoin(){

        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                for (int j = 0; j < 1000; j++) {
                    result += j;
                }
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("end join...");
    }
}
