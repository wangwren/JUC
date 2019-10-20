package juc.c_032_ThreadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * FutureTask 既可以定义任务，又可以保存任务的返回值
 * 这与他实现的接口有关系，他顶层实现了 Runnable和Future接口
 *
 * FutureTask的构造函数接收Callable或者Runnable
 */
public class T04_FutureTask {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //这里的构造参数传入的是Callable接口，因为有返回值
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            TimeUnit.MILLISECONDS.sleep(500);
            return 1000;
        });


        //由于FutureTask实现了Runnable，所以可以使用Thread来执行，执行后的返回结果在futureTask中
        new Thread(futureTask).start();

        System.out.println(futureTask.get());
    }
}
