package juc.c_21;

import java.util.concurrent.Semaphore;

/**
 * 信号量，指定多少个线程可以同时运行
 * @author: wangwren
 */
public class T01_TestSemaphore {

    public static void main(String[] args) {
        //允许 1 个线程运行
        //当指定为 2 时，就允许两个线程同时运行
        //就算线程个数没有指定的个数多也没问题，这个类可以做限流使用
        Semaphore semaphore = new Semaphore(1);

        new Thread(() -> {
            try {
                //acquire是一个阻塞方法，会将指定的个数减1，减为0时，别的线程就需要等待
                semaphore.acquire();

                System.out.println("t1 running...");
                Thread.sleep(1000);
                System.out.println("t1 running end...");

                //当该线程处理完成后，需要将减的1还回去，为了给别的线程使用，如果不还回去，那么别的线程就运行不了，程序也就一直卡在这个线程上
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();


        new Thread(() -> {
            try {
                semaphore.acquire();

                System.out.println("t2 running...");
                Thread.sleep(1000);
                System.out.println("t2 running end...");

                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();
    }



}
