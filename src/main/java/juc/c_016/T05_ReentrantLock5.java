package juc.c_016;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可以指定为公平锁。
 * 即多个线程抢占锁，抢不到的线程就等待，有先来后到的，而不是大家一起去强
 * @author: wangwren
 */
public class T05_ReentrantLock5 extends Thread {

    /**
    * 构造方法指定为true就是公平锁，默认为false，不公平
    *
    * 设置为true时，会发现输出结果是交替的th1-  th2- (并不绝对，有时也会出现多个th1或th2)
    * 设置为false时，会发现输出的结果先是输出多个th1，再是多个th2。
    *
    *  这个公平也并不绝对，但是要有这个概念
    *
    */
    ReentrantLock lock = new ReentrantLock(true);

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + "获得锁...");
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        T05_ReentrantLock5 t = new T05_ReentrantLock5();
        Thread th1 = new Thread(t,"th1-");
        Thread th2 = new Thread(t,"th2-");

        th1.start();
        th2.start();
    }
}
