package juc.c_025_AQS;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: wangwren
 * @date: 2019/10/8
 * @description: juc.c_025_AQS
 * @version: 1.0
 */
public class TestReentrantLock {

    private static volatile int count = 0;

    /**
    * AQS的实现主要在于维护了一个volatile int state的变量和一个FIFO的线程等待队列。
    * 对于state的值，每一个实现类代表的意思都是不同的：
    * 比如ReentrantLock，他的state代表的是锁重入的次数，state值为1时表示获取到了锁，为2时表示锁重入了，即lock了两次。
    * 比如CountDownLatch，他的state代表的是创建时指定的变量的值。
    *
    * 在ReentrantLock中有公平锁和非公平锁，即FairSync和NonfairSync；
    * 而且在jdk1.8与jdk1.9中，ReentrantLock中实现的方式改变了，但是其实现逻辑没变；
    * 在jdk1.9中，多了Varhandle的修饰。
    *
    * ReentrantLock lock过程：(JDK11)
    *  先调用了ReentrantLock 中的 sync.acquire(1);
    *  之后调用了AQS的acquire方法，在这个方法里做了判断，先尝试去获取锁tryAcquire，如果获取到了，就运行完了。
    *  如果没有获取到锁，就会将当前线程加入AQS的队列中，以Node存储，Node中放的就是线程。
    *  加入队列的时候调用addWaiter，加入到队列的尾部，由于有线程安全问题，又不想加锁，就使用了死循环和CAS操作compareAndSetTail(oldTail, node)
    *  https://www.cnblogs.com/takumicx/p/9402021.html
    */
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        CountDownLatch latch = new CountDownLatch(10);

        lock.lock();

        count++;

        lock.unlock();
    }
}
