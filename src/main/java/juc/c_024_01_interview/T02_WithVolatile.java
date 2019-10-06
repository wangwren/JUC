package juc.c_024_01_interview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实现一个容器，提供两个方法，add和size；
 * 写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
 *
 * 在T01_WithoutVolatile中，运行程序会发现根本就不好使，想到的问题是，是不是size的大小线程间不可见。
 * 所以想到在list上加volatile关键字，但是size是list的属性啊，volatile应该是不好使的，看程序吧。
 *
 * 下面的程序有时候能演示出来加上volatile好使，有时候不好使。
 *
 * 给lists添加volatile之后，t2能够接到通知，但是，t2线程的死循环很浪费cpu，如果不用死循环，
 * 而且，如果在if 和 break之间被别的线程打断，得到的结果也不精确
 *
 * @author wangwren
 */
public class T02_WithVolatile {
    //volatile List lists = new ArrayList();
    volatile List lists = Collections.synchronizedList(new LinkedList<>());

    public void add(Object o){
        lists.add(o);
    }

    public int size(){
        return lists.size();
    }

    public static void main(String[] args) {
        T01_WithoutVolatile withoutVolatile = new T01_WithoutVolatile();

        //线程1，添加10个元素
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                withoutVolatile.add(new Object());

                System.out.println("add " + i);
                //System.out.println("size " + withoutVolatile.size());

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");

        t1.start();

        //线程2，监控元素个数，个数达到5个时，线程2给出提示并结束
        Thread t2 = new Thread(() -> {
            System.out.println("t2启动...");
            while (true){
                //死循环监控个数
                if (withoutVolatile.size() == 5){
                    break;
                }
            }

            System.out.println("t2结束...");
        },"t2");

        t2.start();
    }

}
