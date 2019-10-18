package juc.c_030_02_FromVector2Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * 有N张火车票，每张票都有一个编号
 * 同时有10个窗口对外售票
 * 请写一个模拟程序
 *
 * 使用Vector或Collections.synchronizedXXX,内部都加了锁synchronized效果一样
 *
 * 分析下面程序是否有问题
 *
 * @author: wangwren
 */
public class TicketSeller2Vector {

    //票
    //static Vector<String> tickets = new Vector<>();

    static List<String> tickets = Collections.synchronizedList(new ArrayList<String>());

    static {
        //一千张票
        for (int i = 0; i < 1000; i++) {
            tickets.add("票编号：" + i);
        }
    }

    public static void main(String[] args) {
        //10个窗口
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                /*while (tickets.size() > 0){
                    //只要有票就一直循环卖
                    System.out.println("销售了=====" + tickets.remove(0));
                }*/

                while (true){
                    if (tickets.size() <= 0){
                        break;
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //同样也会出现问题，虽然vector加了锁，但是size()放和remove()方法之间不是原子操作
                    //即，多个线程同时达到了size方法处，此时size > 0，之后进入到一段没有加锁的代码，
                    //此时又有线程开始remove,在remove最后一个元素时，正好又有线程到达size，到达size处的线程先执行完，之后remove掉了最后一个元素
                    //这就出问题了，已经没有元素了，剩余的线程再remove就出问题了
                    System.out.println("销售了=====" + tickets.remove(0));
                }
            }).start();
        }
    }
}
