package juc.c_031;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;

/**
 * 跳表：http://blog.csdn.net/sunxianghuang/article/details/52221913
 *
 *
 */
public class T01_ConcurrentHashMap {

    public static void main(String[] args) {
        Random r = new Random();

        //定义100个线程
        Thread[] ths = new Thread[100];
        CountDownLatch latch = new CountDownLatch(ths.length);

        Map<String,String> map = new ConcurrentHashMap<>(); //高并发，但是无序
        //Map<String,String> map = new ConcurrentSkipListMap<>(); //跳表，高并发并且有序
        //Map<String,String> map = new HashMap<>();
        //Map<String,String> map = new Hashtable<>();
        //Map<String,String> map = new TreeMap<>(); //红黑树，但是在并发包里并没有ConcurrentTreeMap，因为树结构做CAS太难了，所以就有了跳表ConcurrentSkipListMap

        long start = System.currentTimeMillis();

        for (int i = 0; i < ths.length; i++) {
            ths[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    map.put("a" + r.nextInt(100000),"a" + r.nextInt(100000));
                }
                latch.countDown();
            });
        }

        Arrays.asList(ths).forEach(t -> t.start());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
}
