package juc.c_000;

/**
 * 线程的sleep、yield、join
 * @author: wangwren
 */
public class T03_Sleep_Yield_Join {
    public static void main(String[] args) {
        //testSleep();
        //testYield();
        testJoin();
    }

    /**
    * 使用sleep方法
    */
    static void testSleep(){
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("A" + i);
                try {
                    //每次睡500毫秒
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
    * yield方法测试
     * yield方法就相当于该线程正处于cpu上运行着，当到达某个条件时，
     * 比如它想去上个厕所，这时候cpu就会被空出来，别的线程可以来抢占，
     * 同时它自己也是可以抢占的，即有可能上个厕所回来了，可以参数抢占
    */
    static void testYield(){
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("A" + i);
                if (i % 10  == 0){
                    //当 i 能被10整除时，调用yield方法
                    //此时该线程会从cpu中空出来，让别的线程去抢占cpu，当然自己也会去抢占
                    //与join方法不同
                    Thread.yield();
                }
            }
        }).start();
        
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("B" + i);
                if (i % 5 == 0){
                    Thread.yield();
                }
            }
        }).start();
    }

    /**
    * 测试join方法
    * join方法是用在自己当前线程调用**别的线程的join方法，不是自己的join方法**
    * join方法与yield方法不同，yield是停一会，自己还可以抢占
    * join方法是直接让出cpu，让别的线程来运行，而且自己等待别的线程执行完毕，才开始继续运行。
    * 这里有个面试题，t1,t2,t3线程如何按顺序执行：
    *   可以在主线程中分别调用三个线程的join方法；当然，更好的处理方法可以是在t1调用t2线程的join方法，
    *   在t2中调用t3线程的join方法，这样就会t3线程执行完才会执行t2线程，t2线程执行完才会执行t1线程。
    */
    static void testJoin(){
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10;i++){
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        Thread t2 = new Thread(() -> {

            //调用t1线程的join方法
            /*try {
                //join方法写在这，那么就是t1线程全部执行完，才会执行t2线程
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            for (int i = 0; i < 10; i++) {
                System.out.println("B" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (i == 5){
                    try {
                        //写在这，在开始时t1，t2可能交替执行，当t2中的i=5时
                        //控制台的输出就全是t1的打印了，当t1打印完毕后才会继续执行t2线程
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //启动两个线程
        t2.start();
        t1.start();
    }
}
