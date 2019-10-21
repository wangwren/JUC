package juc.c_032_ThreadPool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * WorkStealingPool,就是使用的ForkJoinPool来创建的，没啥特别的意思，只是提供一个接口调用
 *
 * @author: wangwren
 */
public class T11_WorkStealingPool {

    public static void main(String[] args) throws IOException {
        ExecutorService service = Executors.newWorkStealingPool();

        //获取当前系统的核数
        System.out.println(Runtime.getRuntime().availableProcessors());

        service.execute(new R(1000));
        service.execute(new R(2000));
        service.execute(new R(2000));
        service.execute(new R(2000));
        service.execute(new R(2000));

        //由于产生的是精灵线程(又叫守护线程或后台线程)，主线程不阻塞的话，看不到输出结果
        System.in.read();
    }

    static class R implements Runnable{

        int time;
        public R(int time){
            this.time = time;
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(time + " " + Thread.currentThread().getName());
        }
    }
}
