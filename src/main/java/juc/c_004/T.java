package juc.c_004;

/**
 * synchronized锁定静态方法
 * @author: wangwren
 */
public class T {

    private static int count = 10;

    public synchronized static void m(){
        //锁在静态方法上相当于锁定的是synchronized(T.class) ,这里锁的Class对象
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

    public static void n(){
        synchronized (T.class){ //这里写this不行，编译报错
            count--;
        }
    }
}
