package juc.c_003;

/**
 * synchronized 关键字
 * 锁定在方法上
 * @author: wangwren
 */
public class T {

    private int count = 10;

    public synchronized void m(){
        //synchronized写在方法上与 synchronized(this)的方式相同，都是锁定当前对象
        //唯一不同的是看synchronized(this)锁定多少代码
        //写在方法上是该方法内的代码都锁，synchronized(this)可以锁定部分代码
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

    public static void main(String[] args) {
        T t = new T();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                t.m();
            }).start();
        }
    }
}
