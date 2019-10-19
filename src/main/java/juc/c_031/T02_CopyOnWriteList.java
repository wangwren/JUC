package juc.c_031;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 写时复制容器 copy on write
 *
 * 多线程环境下，写时效率低，读时效率高
 *
 * 适合读多写少的环境
 *
 * CopyOnWriteArrayList源码中，写时会加锁，并建了一个新的数组，并比之前的数组+1大小
 * 将新元素放入新的位置
 *
 * 读的时候不加锁，因为读的时候，新的和老的元素都一样，不需要加锁。
 *
 * 和ReadWriteLock读写锁差不多
 */
public class T02_CopyOnWriteList {

    public static void main(String[] args) {
        //List<String> list = new ArrayList<>(); //使用ArrayList会出现并发问题
        //List<String> list = Collections.synchronizedList(new ArrayList<>());
        List<String> list = new CopyOnWriteArrayList<>(); //写的时候add，非常慢

        Random random = new Random();
        Thread[] ths = new Thread[100];

        for (int i = 0; i < ths.length; i++) {
            ths[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    list.add("a" + random.nextInt(10000));
                }
            });
        }

        long start = System.currentTimeMillis();

        Arrays.asList(ths).forEach(t -> t.start());
        Arrays.asList(ths).forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        long end = System.currentTimeMillis();

        System.out.println("用时：" + (end - start));

        System.out.println("size = " + list.size());
    }
}
