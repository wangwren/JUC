package juc.c_022;

import java.util.concurrent.Exchanger;

/**
 * 只能是两个线程之间，exchange方法是阻塞的.
 * 一个A线程exchange了，另一个B线程没有exchange，那么A线程就等着，阻塞。
 *
 * 三个线程之间没有意义。
 *
 * 运行观察下面程序
 * @author: wangwren
 */
public class T02_TestExchanger {

    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(() -> {
            String s = "T1";
            try {
                s = exchanger.exchange(s);

                System.out.println(Thread.currentThread().getName() + " " + s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();


        new Thread(() -> {
            String s = "T2";
            try {
                s = exchanger.exchange(s);

                System.out.println(Thread.currentThread().getName() + " " + s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();
    }
}
