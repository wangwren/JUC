package juc.c_032_ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService 继承自Executor
 *
 * 多了很多方法，其中有submit方法，该方法接收一个Callable对象，并返回Future。submit是提交线程用，什么时候执行不知道，异步的
 */
public class T02_ExecutorService {

    public static void main(String[] args) {
        //Executors是一个工具类，类似Collections，他提供了四种默认的线程池
        ExecutorService e = Executors.newCachedThreadPool();
    }
}
