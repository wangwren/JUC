package juc.c_027_ThreadLocal;

/**
 * @author: wangwren
 * @date: 2019/10/12
 * @description: 没用ThreadLocal的情况
 * @version: 1.0
 */
public class ThreadLocal01 {

    volatile static Person person = new Person();

    public static void main(String[] args) {

        //该线程睡两秒之后打印person的名称
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(person.name);
        }).start();


        //该线程睡1秒之后更改person的name值，两个线程使用的是同一个对象，观察最后输出结果
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            person.name = "lisi";
        }).start();

        //最后的输出结果是lisi，逻辑很简单，但是如果我就想让睡两秒的线程打印zhangsan怎么办
        //这时候就需要使用ThreadLocal


    }

}

class Person{
    String name = "zhangsan";
}
