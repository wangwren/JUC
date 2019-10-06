package juc.c_024_02_interview;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 写一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支持2个生产者线程以及10个消费者线程的阻塞调用。
 *
 * 使用wait、notify/notifyAll的方法
 *
 * 以下程序，在使用wait和notify的时候，当消费者通知生产者的时候，无法指定的通知到生产者，也有可能有等待的消费者也接收到了通知。
 *
 * @author wangwren
 * @param <T>
 */
public class MyContainer1<T> {

    private LinkedList<T> lists = new LinkedList();

    //最多10个元素
    private static final int MAX = 10;

    private int count = 0;

    /**
     * 必须使用同步，因为有count++的操作，不是线程安全的
     * @param t
     */
    public synchronized void put(T t){
        //考虑一下这里为什么用while。不能使用if，因为当被唤醒时也需要检查数量，如果用if就直接向下执行了，如果有新的加入怎么办，所以需要检查
        while (lists.size() == MAX){
            //如果集合中已满，就不再生产了
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        lists.add(t);
        count++;

        //通知消费者消费
        this.notifyAll();
    }

    public synchronized T get(){
        while (lists.size() == 0){
            //如果集合中没有元素了，消费者不再消费了，等待生产者生产
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        T t = lists.removeFirst();
        count--;
        //通知生产者生产
        this.notifyAll();
        return t;
    }

    public static void main(String[] args) {
        MyContainer1<String> container1 = new MyContainer1<>();

        //启动消费者线程consumer,创建10个消费者，一个消费者消费5个
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    System.out.println(container1.get());
                }
            },"c" + i).start();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //启动生产者线程produce，创建2个生产者，一个生产者生产25个
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 25; j++) {
                    container1.put(Thread.currentThread().getName() + " " + j);
                }
            },"p" + i).start();
        }
    }

}
