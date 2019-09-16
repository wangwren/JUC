package juc.c_016;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock可用来替代synchronized
 * 对于T01_ReentrantLock1类中的方法，使用ReentrantLock同样可以实现
 * 但是ReentrantLock 必须要必须要必须要 手动释放锁。
 *
 * synchronized在遇到异常时会自动释放锁，ReentrantLock释放经常写在finally中，防止遇到异常情况锁还没有释放
 * @author: wangwren
 */
public class T02_ReentrantLock2 {

    ReentrantLock lock = new ReentrantLock();

    void m1(){
        try {
            //加锁
            lock.lock(); //synchronized(this)

            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(i);
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //释放锁
            lock.unlock();
        }
    }

    void m2(){
        try {
            lock.lock();
            System.out.println("m2...");
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        T02_ReentrantLock2 t = new T02_ReentrantLock2();

        new Thread(t::m1).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(t::m2).start();
    }
}
