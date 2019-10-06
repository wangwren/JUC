package juc.c_024_02_interview;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 写一个固定容量同步容器，拥有put和get方法，以及getCount方法，
 * 能够支持2个生产者线程以及10个消费者线程的阻塞调用。
 *
 * 使用Lock和Condition的方式来实现
 * 对比上一个程序，Condition的方式可以更加精准的指定哪些线程被唤醒
 *
 * @author wangwren
 */
public class MyContainer2<T> {

    private LinkedList<T> lists = new LinkedList<>();

    private static final int MAX = 10;

    private int count;

    private Lock lock = new ReentrantLock();

    //使用Condition，条件，这里指定消费者，其实就是指定了一个等待队列，这里放的都是消费者
    private Condition consumer = lock.newCondition();
    //使用Condition，条件，这里指定生产者，其实就是又指定了一个等待队列，这里放的都是生产者
    private Condition produce = lock.newCondition();

    public void put(T t){
        try {
            lock.lock();
            while (lists.size() == MAX) {
                //集合满了，生产者等待
                produce.await();
            }

            lists.add(t);
            count++;

            //通知到消费者消费
            consumer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //lock的解锁一定要放在finally中
            lock.unlock();
        }

    }

    public T get(){
        T t = null;
        try {
            lock.lock();
            while (lists.size() == 0) {
                //集合为0，消费者等待
                consumer.await();
            }
            t = lists.removeFirst();
            count--;

            //通知生产者生产
            produce.signalAll();
        } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        return t;
    }

    public static void main(String[] args) {
        MyContainer2<String> container2 = new MyContainer2<>();

        //启动消费者线程consumer,创建10个消费者，一个消费者消费5个
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    System.out.println(container2.get());
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
                    container2.put(Thread.currentThread().getName() + " " + j);
                }
            },"p" + i).start();
        }
    }
}
