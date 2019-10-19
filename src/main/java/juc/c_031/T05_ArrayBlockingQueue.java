package juc.c_031;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class T05_ArrayBlockingQueue {

    static BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    Random random = new Random();

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            queue.put("a" + i);
        }

        //满了就会等待，程序阻塞
        //queue.put("aaa");

        //满了会报错
        //queue.add("bbb");

        //满了就不加了，返回false
        queue.offer("ccc");

        System.out.println(queue);
    }
}
