package juc.c_024_01_interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实现一个容器，提供两个方法，add和size；
 * 写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
 *
 * 分析下面这个程序是否能够达到效果
 * @author wangwren
 */
public class T01_WithoutVolatile {

    List lists = new ArrayList();

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
