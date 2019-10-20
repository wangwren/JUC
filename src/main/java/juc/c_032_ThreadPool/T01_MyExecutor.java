package juc.c_032_ThreadPool;

import java.util.concurrent.Executor;

/**
 * Executor 翻译过来就是执行的意思
 *
 * 有一个execute方法，接收Runnable
 */
public class T01_MyExecutor implements Executor {

    public static void main(String[] args) {
        new T01_MyExecutor().execute(() -> System.out.println("hello executor"));
    }

    @Override
    public void execute(Runnable command) {
        //写了这句，Runnable才会执行
        command.run();
    }
}
