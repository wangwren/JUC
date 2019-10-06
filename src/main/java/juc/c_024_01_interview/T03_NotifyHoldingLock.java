package juc.c_024_01_interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实现一个容器，提供两个方法，add和size；
 * 写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
 *
 * 使用volatile根本就不能解决问题。
 *
 * 所以使用wait和notify能做到。需要注意的是：wait会释放锁，notify不会释放锁。
 * 使用这种方法需要注意的是，必须要保证t2线程先执行，也就是首先让t2监听上才可以。
 *
 * 看下面程序，最后运行结果并不是在size为5的时候t2执行退出，而是t1结束时t2才接收到通知而退出。
 *
 * @author wangwren
 */
public class T03_NotifyHoldingLock {

    List list = new ArrayList();

    public void add(Object o){
        list.add(o);
    }

    public int size(){
        return list.size();
    }


    public static void main(String[] args) {
        T03_NotifyHoldingLock notifyHoldingLock = new T03_NotifyHoldingLock();

        final Object lock = new Object();

        Thread t2 = new Thread(() -> {
            synchronized (lock){
                System.out.println("t2启动...");
                if (notifyHoldingLock.size() != 5){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("t2结束...");
            }
        },"t2");

        t2.start();

        Thread t1 = new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < 10; i++) {
                    notifyHoldingLock.add(new Object());

                    System.out.println("add " + i);

                    if (notifyHoldingLock.size() == 5){
                        lock.notify();
                    }

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();
    }
}
