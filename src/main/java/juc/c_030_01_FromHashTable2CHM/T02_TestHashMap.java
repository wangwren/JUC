package juc.c_030_01_FromHashTable2CHM;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

/**
 * HashMap性能测试
 * HashTable在设计上有问题，之后才出现了HashMap
 * @author: wangwren
 */
public class T02_TestHashMap {

    static HashMap<UUID,UUID> m  = new HashMap<>();

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

        for (int i = 0; i < threads.length; i++) {
            //创建一百个线程
            threads[i] = new MyThread((i * (count / THREAD_COUNT)));
        }

        //启动线程
        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("HashMap 存放元素：" + (end - start));

        //HashMap不是线程安全的，所以多线程添加元素时，必会出问题
        System.out.println("元素个数：" + m.size());

        //HashMap存放元素有线程安全问题，所以对于获取元素的测试就没意义了，元素个数都不准
    }
}
