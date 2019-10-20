package juc.c_032_ThreadPool;

import java.util.concurrent.*;

/**
 * Callable 内只有一个call方法，与Runnable很像，唯一不同的是，Callable有返回值
 *
 * 一般多用在线程池中
 */
public class T03_Callable {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> callable = new Callable() {
            @Override
            public String call() throws Exception {
                return "hello callable";
            }
        };

        ExecutorService e = Executors.newCachedThreadPool();

        //会有返回值，并且submit是异步的，提交后主线程会继续执行
        Future<String> future = e.submit(callable);

        //future.get()方法是阻塞的，会一直等待返回值
        System.out.println(future.get());

        //关闭线程池，否则程序会一直运行
        e.shutdown();
    }
}
