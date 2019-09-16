package juc.c_016;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock，还可以调用lockInterruptibly方法获取锁，该方式可以在等待锁的过程中，呗interrupt方法打断。
 *
 * lockInterruptibly()方法比较特殊，当通过这个方法获取锁时(这个方法也是可以获取锁的，只是在获取锁的过程中，有点别的动作)，
 * 如果其他线程正在等待锁，则这个线程能够响应中断，即中断线程的等待状态。
 * 也就是说，当两个线程都调用lockInterruptibly()获取某个锁时，假若此时A线程获取到了锁，B线程等待，那么对线程B 调用threadB.interrupt()
 * 方法能够中断线程B的等待过程。
 *
 * 注意：等待的那个线程B可以中断，而不是正在执行的线程A被中断。
 * @author: wangwren
 */
public class T04_ReentrantLock4 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("t1 satrt...");
                //一直都在睡
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
                System.out.println("t1 end...");
            } catch (InterruptedException e) {
                System.out.println("t1 interrupt...");
            } finally {
                lock.unlock();
            }
        },"t1");

        t1.start();

        Thread t2 = new Thread(() -> {
            try {
                lock.lockInterruptibly(); //可以对interrupt方法做出响应
                System.out.println("t2 satrt...");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("t2 end...");

            } catch (InterruptedException e) {
                System.out.println("t2 interrupt...");
            } finally {
                lock.unlock();
            }
        },"t2");

        t2.start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //中断t2线程
        t2.interrupt();
    }
}
