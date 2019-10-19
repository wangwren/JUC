package juc.c_031;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * 这个Queue容量为0，用来两个线程间同步交换
 */
public class T08_SynchronousQueue {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new SynchronousQueue<>();

        //开启一个线程去获取元素，否则会一直阻塞
        new Thread(() -> {
            try {
                System.out.println(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        //如果把上面的线程注释掉，则会一直阻塞
        queue.put("aaa");

        //使用add会报错，因为这个Queue容量为0啊
        //queue.add("bbb");

        System.out.println(queue.size());
    }
}
