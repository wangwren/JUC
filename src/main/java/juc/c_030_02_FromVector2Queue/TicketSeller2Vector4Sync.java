package juc.c_030_02_FromVector2Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 有N张火车票，每张票都有一个编号
 * 同时有10个窗口对外售票
 * 请写一个模拟程序
 *
 * 使用Vector或Collections.synchronizedXXX,内部都加了锁synchronized效果一样
 *
 * 分析下面程序是否有问题
 *
 * 解决上一个程序的问题：
 *  就算操作A和操作B都是同步的，但A和B组成的复合操作也未必是同步的，仍然需要自己进行同步。
 *  就像这个程序，判断size和remove必须是一整个原子操作。
 *
 * @author: wangwren
 */
public class TicketSeller2Vector4Sync {

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
                    //加锁
                    synchronized (tickets){
                        if (tickets.size() <= 0){
                            break;
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("销售了=====" + tickets.remove(0));
                    }

                }
            }).start();
        }
    }
}
