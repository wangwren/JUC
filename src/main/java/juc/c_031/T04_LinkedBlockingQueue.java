package juc.c_031;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞队列BlockingQueue,天生的生产者消费者模型
 */
public class T04_LinkedBlockingQueue {

    static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    static Random random = new Random();

    public static void main(String[] args) {

        //生产者
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    //put是BlockingQueue新加的方法，与offer和add的区别是，put加满会阻塞，加满就等待
                    queue.put("a" + i);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"p1").start();


        //消费者,创建5个消费者
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (;;){
                    try {
                        //take是BlockingQueue新加的方法，与poll和peek的区别是，take取空了会阻塞，等待
                        System.out.println(Thread.currentThread().getName() + " take - " + queue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"c" + i).start();
        }

    }
}
