package juc.c_030_02_FromVector2Queue;

import java.util.ArrayList;
import java.util.List;

/**
 * 有N张火车票，每张票都有一个编号
 * 同时有10个窗口对外售票
 * 请写一个模拟程序
 *
 * 使用ArrayList
 *
 * 分析下面程序是否有问题
 *
 * @author: wangwren
 */
public class TicketSeller2ArrayList {

    //票
    static List<String> tickets = new ArrayList<>();

    static {
        //一万张票
        for (int i = 0; i < 10000; i++) {
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

                    //必出问题，ArrayList不是线程安全的，多线程同时访问一个tickets，肯定会出问题。票卖重了，票卖多了
                    System.out.println("销售了=====" + tickets.remove(0));
                }
            }).start();
        }
    }
}
