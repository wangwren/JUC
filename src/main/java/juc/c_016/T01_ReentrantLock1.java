package juc.c_016;

import java.util.concurrent.TimeUnit;

/**
 * 重入锁
 *
 * 复习synchronized，synchronized也是可重入的
 * ReentrantLock可用来替代synchronized
 * @author: wangwren
 */
public class T01_ReentrantLock1 {

    synchronized void m1(){
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(i);

            if (i == 2){
                //在m1中调用加锁的m2方法，由于是同一把锁，所以可以调用，也代表了synchronized可重入
                m2();
            }
        }
    }

    synchronized void m2(){
        System.out.println("m2...");
    }


    public static void main(String[] args) {
        T01_ReentrantLock1 t = new T01_ReentrantLock1();

        new Thread(t::m1).start();
    }
}
