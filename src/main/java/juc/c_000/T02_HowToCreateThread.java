package juc.c_000;

/**
 * 线程创建的几种方式
 *
 *
 * 面试题：请告诉我启动(创建)线程的几种方式：1.Thread 2.Runnable 3.线程池 Executors.newCachedThrad
 * @author wangwren
 */
public class T02_HowToCreateThread {

    /**
     * 继承Thread，重写run方法
     */
    static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println("Hello MyThread!!!");
        }
    }

    /**
     * 实现runnable接口，实现run方法
     */
    static class MyRunnable implements Runnable{

        @Override
        public void run() {
            System.out.println("Hello MyRunnable!!!");
        }
    }

    public static void main(String[] args) {
        //第一种启动线程
        new MyThread().start();

        //第二种启动线程
        new Thread(new MyRunnable()).start();

        //第三种启动线程，使用Java8的特性。其实这种方式与第二种差不多
        new Thread(() -> {
            System.out.println("Hello Lambda");
        }).start();
    }

}
