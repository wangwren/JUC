package juc.c_025_AQS;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: wangwren
 * @date: 2019/10/8
 * @description: juc.c_025_AQS
 * @version: 1.0
 */
public class TestReentrantLock {

    private static volatile int count = 0;

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        lock.lock();

        count++;

        lock.unlock();
    }
}
