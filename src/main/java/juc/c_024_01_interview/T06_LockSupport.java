package juc.c_024_01_interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * 实现一个容器，提供两个方法，add和size；
 * 写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
 *
 * 使用LockSupport实现
 *
 * @author wangwren
 */
public class T06_LockSupport {

    List list = new ArrayList();

    public void add(Object o){
        list.add(o);
    }

    public int size(){
        return list.size();
    }

    public static void main(String[] args) {

        T06_LockSupport t06_lockSupport = new T06_LockSupport();

        Thread t2 = new Thread(() -> {
            System.out.println("t2启动...");

            LockSupport.park();

            System.out.println("t2结束...");
        });

        t2.start();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                t06_lockSupport.add(new Object());

                System.out.println("add " + i);

                if (t06_lockSupport.size() == 5){
                    LockSupport.unpark(t2);
                }

                //如果不睡这一秒，还是会出现CountDownLatch那种现象，所以想要阻止就需要两个LockSupport
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
