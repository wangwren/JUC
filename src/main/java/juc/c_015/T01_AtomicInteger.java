package juc.c_015;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解决c_012 VolatileNotSync 的问题
 *
 * 解决同样的问题的更高效的方法，使用AtomXXX类
 * AtomXXX类本身方法都是原子性的，但不能保证多个方法连续调用是原子性的
 * @author wangwren
 */
public class T01_AtomicInteger {

    //volatile int count = 0;

    //使用原子类操作
    AtomicInteger count = new AtomicInteger(0);

    public /*synchronized*/ void m(){
        for (int i = 0; i < 10000; i++) {

            //incrementAndGet是原子操作
            count.incrementAndGet();//count++
        }
    }

    public static void main(String[] args) {
        T01_AtomicInteger t = new T01_AtomicInteger();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //添加十个线程
            threads.add(new Thread(t::m, "thread-" + i));
        }

        //运行十个线程,
        threads.forEach(o -> o.start());

        threads.forEach(o -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        //看上面的程序，最终结果应该是 10 * 10000
        System.out.println(t.count);
    }
}
