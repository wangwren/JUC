package juc.c_015;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * AtomicXXX Sync加锁方式  LongAdder 三种方式性能比较
 *
 * 运行下面程序，查看结果
 * @author: wangwren
 */
public class T02_AtomicVsSyncVsLongAdder {

    static AtomicLong count1 = new AtomicLong(0L);
    static long count2 = 0L;

    /**
    * LongAdder也时atomic包下的，该类在极高的并发量或大数据量下很有优势。
    * 该类内部使用的是分段的形式，之后合并求和的方式
    */
    static LongAdder count3 = new LongAdder();

    public static void main(String[] args) {
        Thread[] threads = new Thread[1000];

        //atomic
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    count1.incrementAndGet();
                }
            });
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("atomic ---- count1=" + count1.get() + "---- time=" + (System.currentTimeMillis() - start));


        //sync
        Object lock = new Object();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    synchronized (lock){
                        //只在需要加锁的地方加锁，细粒化锁
                        count2++;
                    }
                }
            });
        }

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("sync ------ count2=" + count2 + "---- time=" + (System.currentTimeMillis() - start2));

        //LongAdder
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    count3.increment();
                }
            });
        }

        long start3 = System.currentTimeMillis();

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("LongAdder - count3=" + count3.longValue() + "---- time=" + (System.currentTimeMillis() - start3));

    }
}
