package juc.c_030_01_FromHashTable2CHM;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap
 * JDK中最先出现的就是HashTable容器之后一点点演进到ConcurrentHashTable
 *
 * 进化过程是由HashMap -> SynchronizedHashMap
 *
 * ConcurrentHashMap的好处体现在其获取元素非常快，并且添加元素线程安全，对比另两个，他们获取元素时就太慢了
 * @author: wangwren
 *
 */
public class T04_TestSynchronizedHashMap {

    //Collections是一个工具，通过这个接口可以生成线程安全的list、map、set
    //synchronizedMap 内部结构与HashTable差不多，性能也差不多，也是加了synchronized
    //进化过程是由HashMap -> SynchronizedHashMap
    static Map<UUID,UUID> m = new ConcurrentHashMap<>();

    static int count = Constants.COUNT;
    static UUID[] keys = new UUID[count];
    static UUID[] values = new UUID[count];

    static final int THREAD_COUNT = Constants.THREAD_COUNT;
    
    static {
        for (int i = 0; i < count; i++) {
            keys[i] = UUID.randomUUID();
            values[i] = UUID.randomUUID();
        }
    }

    static class MyThread extends Thread{

        //存入的起始位置
        int start;
        //存入的数量 10000 个，即每个线程存10000个
        int gap = count / THREAD_COUNT;

        public MyThread(int start){
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = start; i < start + gap; i++) {
                m.put(keys[i],values[i]);
            }
        }
    }

    public static void main(String[] args) {

        //---------存放元素测试
        long start = System.currentTimeMillis();

        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            //创建一百个线程
            threads[i] = new MyThread((i * (count / THREAD_COUNT)));
        }

        //启动线程
        for (Thread t : threads) {
            t.start();
        }
        
        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long end = System.currentTimeMillis();

        //由于ConcurrentHashMap内部添加元素十分麻烦，所以在添加元素时用的时长比HashTable和SynchronizedHashMap用时长
        //ConcurrentHashMap内部是通过CAS实现的线程安全，在添加元素时还有转成红黑树的操作，所以时间长
        System.out.println("ConcurrentHashMap 存放元素：" + (end - start));

        System.out.println("元素个数：" + m.size());


        //--------读取元素测试

        start = System.currentTimeMillis();

        for (int i = 0; i < threads.length; i++) {
            //创建100个线程，每个线程循环100000000次获取指定的位置的元素
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000000; j++) {
                    m.get(keys[10]);
                }
            });
        }

        for (Thread t : threads){
            t.start();
        }

        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        end = System.currentTimeMillis();

        //ConcurrentHashMap在获取元素时，用的时间最短
        System.out.println("ConcurrentHashMap获取元素时间：" + (end - start));
    }
}
