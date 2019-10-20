package juc.c_032_ThreadPool;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class T06_HelloThreadPool {

    static class Task implements Runnable{

        private int i;

        public Task(int i){
            this.i = i;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " Task " + i);
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Task{" +
                    "i=" + i +
                    '}';
        }
    }


    public static void main(String[] args) {

        //阿里巴巴手册中规定，不允许使用Executors创建线程池，而是通过ThreadPoolExecutor的方式创建
        //这样的处理方式能让编写代码的工程师更加明确线程池的运行规则，规避资源耗尽的风险

        //使用ThreadPoolExecutor就需要知道构造函数中参数中的意思，背吧

        /**
         * 第一个参数，corePoolSize:核心线程的数量，这里指定为2个
         *
         * 第二个参数，maximumPoolSize:最大线程的数量，这里指定为4个，即一共就有四个，带上核心的，一共四个
         *
         * 第三个参数，keepAliveTime:最大线程放回操作系统的时间，即如果线程数多于两个，肯定是申请了最大线程数量了，当最大线程数量(即另外两个线程)
         *           空闲60秒的时候，就还给操作系统，但是不会归还核心的两个线程
         *
         * 第四个参数，指定线程空闲的时间单位
         *
         * 第五个参数，指定队列，任务队列，这里指定阻塞队列(笔记有记过)，当线程数量特别多时，比如已经占用了核心的两个线程，这时候又来线程，
         *           那么，新来的线程会被存放在阻塞队列中，这个阻塞队列大小为4个，当4个满的时候，再来线程，才会申请最大线程数，即maximumPoolSize
         *
         * 第六个参数，线程工厂，产生线程的，这里写的是默认的，JDK提供的，可以看其源码都干了啥，但是实战中一般都自己指定，实现ThreadFactory接口就可以
         *
         * 第七个参数，拒绝策略，当最大的线程数maximumPoolSize都满了的时候，这时候再来线程，就需要拒绝了，JDK默认提供了四种拒绝方式。
         *           但是一般不用，还是自己写，将多的任务放入kafka或者Redis中。
         *           这里指定的是JDK提供的拒绝策略，将等待时间最长的扔掉
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2,4,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());


        for (int i = 0; i < 8; i++) {
            //指定了8个任务，直接将线程池干满
            executor.execute(new Task(i));
        }

        System.out.println("第一次满后队列中的任务：" + executor.getQueue());

        //满后再往线程池中加任务
        executor.execute(new Task(100));

        System.out.println("满后加入新的任务队列中的任务：" + executor.getQueue());

        executor.shutdown();

        /**
         * 输出结果：
         *
         * pool-1-thread-3 Task 6
         * pool-1-thread-4 Task 7
         * pool-1-thread-1 Task 0
         * pool-1-thread-2 Task 1
         * 第一次满后队列中的任务：[Task{i=2}, Task{i=3}, Task{i=4}, Task{i=5}]
         * 满后加入新的任务队列中的任务：[Task{i=3}, Task{i=4}, Task{i=5}, Task{i=100}]
         *
         *
         * 观察这结果，0，1，6，7都被执行了，即使核心线程2个和最大线程2个，一共四个执行的
         * 看队列中的和执行的，也能看出核心的满后先放入队列，队列满后才是最大线程数去执行
         * 由于定义的Task是阻塞的，有System.in.read(); 卡在这了，能看出效果。
         *
         * 第一次满后队列中的任务有 2，3，4，5
         *
         * 加入一个后是 3，4，5，100 有了拒绝策略，将 2 扔掉了
         */
    }
}
