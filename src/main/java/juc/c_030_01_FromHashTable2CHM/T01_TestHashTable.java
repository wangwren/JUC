package juc.c_030_01_FromHashTable2CHM;

import java.util.Hashtable;
import java.util.UUID;

/**
 * HashTable性能测试
 * JDK中最先出现的就是HashTable容器之后一点点演进到ConcurrentHashTable
 * @author: wangwren
 */
public class T01_TestHashTable {

    static Hashtable<UUID,UUID> m  = new Hashtable<>();

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

        System.out.println("HashTable 存放元素：" + (end - start));

        //因为HashTable内部方法都加上了synchronized，所以在多线程添加元素时不会有问题
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

        //巨慢
        System.out.println("HashTable获取元素时间：" + (end - start));
    }
}
