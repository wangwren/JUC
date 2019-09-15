package juc.c_014;

import java.util.concurrent.TimeUnit;

/**
 *  锁定某对象o，如果o的属性发生改变，不影响锁的使用
 *  但是如果o变成另外一个对象，则锁定的对象发生改变
 *  应该避免将锁定对象的引用变成另外的对象，如何避免：为锁定的对象加上final修饰
 * @author wangwren
 */
public class SyncSameObject {

    /*final*/ Object o = new Object();

    void m(){
        synchronized (o){
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) {

        SyncSameObject syncSameObject = new SyncSameObject();

        //启动线程1
        new Thread(syncSameObject::m,"t1").start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //创建线程2
        Thread t2 = new Thread(syncSameObject::m,"t2");

        //改变 o 的引用,锁对象发生改变，所以t2得以执行，如果注释掉这句话，那么t2将永远不会执行
        //换了对象，但是之前锁t1的对象也还在，并没有将t1时的锁释放，所以结果就会出现t1、t2交替在进行
        syncSameObject.o = new Object();

        t2.start();
    }
}