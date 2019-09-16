package juc.c_016;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可以进行尝试锁定tryLock，返回一个Boolean值
 * 这样在无法锁定，或者指定时间内无法锁定，线程可以决定是否继续等待
 * @author: wangwren
 */
public class T03_ReentrantLock3 {

    Lock lock = new ReentrantLock();

    void m1(){
        try {
            lock.lock();
            //循环10次，m2只尝试拿锁5秒，导致m2()永远不会执行，改一下，改成3次
            for (int i = 0; i < 3; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(i);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    void m2(){
        boolean b = false;
        try {
            //尝试锁定，在5秒 之内 看能不能拿到锁
            b = lock.tryLock(5, TimeUnit.SECONDS);
            if (b){
                //如果不加这层判断，5秒之后没拿到锁，还是会执行以下代码，我感觉这样就有点别扭
                //拿到没拿到都会执行，所以就给加了层判断，只有拿到锁时才会去执行以下代码

                //如果拿到锁了，就会执行以下代码，否则放弃拿锁，不再执行
                System.out.println("m2...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (b){
                //如果拿到锁了，再进行释放，如果这里不做判断的话
                //如果没有拿到锁，还进行释放锁的话，会报错
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {

        T03_ReentrantLock3 t = new T03_ReentrantLock3();

        new Thread(t::m1).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(t::m2).start();
    }
}
