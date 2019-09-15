package juc.c_012;

import java.util.ArrayList;
import java.util.List;

/**
 * volatile只能保证可见性，并不能保证多个线程共同修改running变量时带来的不一致问题，也就是说volatile不能替代synchronized。
 *
 * 以下程序，正常的输出结果应该是 10 * 10000，但是多运行几次，会遇到不是10000的情况。即Volatile不能替代synchronized。
 *
 * 比如：线程1在改变了count的值为1时，线程2、3都发现了，都会将count=1的值拿来再做count++操作，这时再写会主内存，这就出现了问题
 *
 * 如果想不出现问题，那么就需要在 m() 方法上加上 synchronized 关键字
 * @author wangwren
 */
public class VolatileNotSync {

    volatile int count = 0;

    /**
     * 在 m 方法上加了synchronized关键字后，count变量就可以不加volatile关键字了
     */
    public /*synchronized*/ void m(){
        for (int i = 0; i < 10000; i++) {
            //count++ 操作并不是原子操作
            count++;
        }
    }

    public static void main(String[] args) {
        VolatileNotSync vns = new VolatileNotSync();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            //添加十个线程
            threads.add(new Thread(vns::m,"thread-" + i));
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
        System.out.println(vns.count);
    }
}
