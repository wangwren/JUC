package juc.c_010;


/**
 * 一个同步方法可以调用另外一个同步方法，一个线程已经拥有某个对象的锁，再次申请的时候仍然会得到该对象的锁。
 *
 * 也就是说synchronized获得的锁是可重入的。
 *
 * 这里是继承中有可能发生的情形，子类调用父类的同步方法
 * @author wangwren
 */
public class T {

    public synchronized void m(){
        System.out.println(Thread.currentThread().getName() + "m() start...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "m() end...");
    }

    public static void main(String[] args) {
        new TT().m();
    }
}


class TT extends T{
    @Override
    public synchronized void m() {
        System.out.println(Thread.currentThread().getName() + "child m ... start");
        //子类去调用父类加锁的方法
        super.m();
        System.out.println(Thread.currentThread().getName() + "child m ... end");
    }
}