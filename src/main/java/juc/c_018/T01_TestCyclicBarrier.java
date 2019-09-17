package juc.c_018;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 栅栏，当等待的线程数等于栅栏初始指定的值时，就可以推翻栅栏，这几个线程同时执行
 *
 * 读下面程序。
 *
 * 在实际应用中，我可以将之前用的CountDownLatch来进行的下载操作改造成使用CyclicBarrier形式。
 * 即11个线程都处理完毕自己的业务后，一起执行打包下载操作
 * @author: wangwren
 */
public class T01_TestCyclicBarrier {
    public static void main(String[] args) {
        //指定当有20个线程时，就可以发车了
        //第一个参数指定多少个线程；第二个参数是接一个Runnable接口，指定当达到20时如何处理，写对应的业务逻辑
        //第二个参数也可以不传，不传就代表当满足条件时，什么都不做
        CyclicBarrier barrier = new CyclicBarrier(20
                ,() -> System.out.println("满人，发车"));

        for (int i = 0; i < 100; i++) {
            //创建100个线程
            new Thread(() -> {
                try {
                    //设置等待，这之前可以写对应的业务逻辑，当满足线程数时，就可以出发之后的逻辑了
                    //当不满足线程数时，那就一直等着；也可以调用另一个await方法，参数接收等待的时间
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
