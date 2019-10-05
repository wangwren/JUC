package juc.c_023;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 锁支持
 *
 * @author wangwren
 */
public class TestLockSupport {

    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);

                if (i == 5){
                    //表示等于5的时候，当前线程就停止了，park意思是停车，就会一直阻塞
                    LockSupport.park();
                }

                if (i == 8){
                    //park两次，先unpark一次，看会不会阻塞。
                    //运行后，等于5的时候不会阻塞；在等于8的时候阻塞了。即unpark和park低效了一次。
                    LockSupport.park();
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //之前很难控制说 指定某一个线程阻塞，但是现在可以了，就是使用LockSupport
        t.start();

        //在主线程中不让它阻塞了，主线程8秒钟之后才不让它阻塞
//        try {
//            TimeUnit.SECONDS.sleep(8);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //这里可以看到，LockSupport解开某一个线程是可以指定某一个线程的，也就是这里的 t
        //unpark(t)是可以在park之前调用的，如果不睡8秒，那么主线程中的子线程启动和unpark会同时执行，即不允许子线程在i==5的时候停车
        //比较之前的线程等待，notify和wait，线程没有wait，你调用是notify是没有任何意义的，这就是区别
        LockSupport.unpark(t);
    }
}
