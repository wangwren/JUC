package juc.c_024_01_interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 实现一个容器，提供两个方法，add和size；
 * 写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
 *
 * 上一个程序通过wait，notify的形式实现了。但是通信过程比较繁琐。
 *
 * 这个程序通过门闩来实现。
 * 使用门闩方式来代替wait和notify的好处是通信方式简单。
 *
 * CountDownLatch不涉及锁定，当count的值为0的时候当前线程继续执行。
 * 当不涉及同步，只是涉及线程间通信时，用synchronized + wait/notify 就显得太重了。
 *
 * 这时应该考虑：countdownlatch/cyclicbarrier/semaphore
 *
 * @author wangwren
 */
public class T05_CountDownLatch {

    List list = new ArrayList();

    public void add(Object o){
        list.add(o);
    }

    public int size(){
        return list.size();
    }

    public static void main(String[] args) {

        T05_CountDownLatch t05_countDownLatch = new T05_CountDownLatch();

        CountDownLatch latch = new CountDownLatch(1);

        Thread t2 = new Thread(() -> {
            System.out.println("t2启动...");

            if (t05_countDownLatch.size() != 5){
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("t2结束....");
        });

        t2.start();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                t05_countDownLatch.add(new Object());

                System.out.println("add " + i);

                if (t05_countDownLatch.size() == 5){
                    latch.countDown();
                }

                //当注释掉睡眠1秒时，后台打印出的t2结束...可能不是在第5个之前，可能是在第7，8，9个
                //那是因为在t1唤醒t2时，t2没有抢占到cpu，等抢占到cpu的时候已经不是第五个了。
                //如果不想睡眠，也能输出对应正确的结果，那么就需要两个CountDownLatch，在t1唤醒后，在await一下
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        t1.start();
    }
}
