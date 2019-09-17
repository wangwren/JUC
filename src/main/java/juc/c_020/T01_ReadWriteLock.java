package juc.c_020;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁(共享锁，排他锁、互斥锁)
 * @author: wangwren
 */
public class T01_ReadWriteLock {

    //正常的锁
    static ReentrantLock lock = new ReentrantLock();

    //获取读写锁
    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    //获取读锁
    static Lock readLock = readWriteLock.readLock();

    //获取写锁
    static Lock writeLock = readWriteLock.writeLock();

    private static int value = 0;

    /**
    * 模拟读取数据
    */
    public static void read(Lock lock){
        //读的时候加锁
        try {
            lock.lock();
            Thread.sleep(1000);
            System.out.println("read over ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
    * 模拟写操作
    */
    public static void write(int val,Lock lock){
        try {
            lock.lock();
            Thread.sleep(1000);
            value = val;
            System.out.println("wirte over ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        //读，传入的是一个ReentrantLock
        //Runnable readR = () -> read(lock);

        //读操作，传入一个读锁，能够提高效率
        Runnable readR = () -> read(readLock);

        //写，传入的也是一个ReentrantLock
        //Runnable writeR = () -> write(new Random().nextInt(),lock);

        //写操作，传入一个写锁
        Runnable writeR = () -> write(new Random().nextInt(),writeLock);

        //读写锁同时操作时，什么时候轮到写锁开始写，是cpu的问题，看写锁能不能抢占到cpu了
        //改造后，即分别传入读写锁后，读操作就很快了，写操作还是需要等待获取锁的。

        //在读写都传入ReentrantLock的情况下，读的操作也被加了锁，那么这种情况下，
        //整个程序运行完，需要20秒，因为读写方法都睡了1秒，别的线程读的时候，也需要获取到锁

        for (int i = 0; i < 18; i++) {
            new Thread(readR).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(writeR).start();
        }
    }
}
