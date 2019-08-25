package juc.c_011;


import java.util.concurrent.TimeUnit;

/**
 * 程序在执行过程中，如果出现异常，默认情况 锁 会被释放
 * 所以，在并发处理的过程中，有异常要多加小心，不然可能会发生不一致的情况。
 * 比如，在一个web app处理过程中，多个servlet线程共同访问同一个资源，这时如果异常处理不合适，
 * 在第一个线程中抛出异常，其他线程就会进入同步代码区，有可能会访问到异常产生时的数据。
 * 因此要非常小心的处理同步业务逻辑中的异常
 * @author wangwren
 */
public class T {

    private int count;

    public synchronized void m(){
        System.out.println(Thread.currentThread().getName() + "start...");

        while (true){
            count ++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " count=" + count);

            if (count == 5){
                //手动模拟一个异常
                int i = 1 / 0;
                System.out.println(i);
            }
        }
    }

    /**
     * 当t1线程执行到count=5时，会抛出一个异常，这时t1抢到的锁被释放，被t2线程执行，由于访问的是同一个资源，t2会接着继续执行
     * @param args
     */
    public static void main(String[] args) {
        T t = new T();

        new Thread(t::m,"t1").start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //t1，t2访问的是同一个对象，即同一个资源
        new Thread(t::m,"t2").start();
    }
}
