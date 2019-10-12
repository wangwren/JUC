package juc.c_027_ThreadLocal;

/**
 * ThreadLocal线程局部变量
 *
 * ThreadLocal是使用空间换时间，synchronized是使用
 *
 * 比如在hibernate中session就存在与ThreadLocal中，避免synchronized的使用
 *
 * 运行下面的程序，理解ThreadLocal
 *
 * @author: wangwren
 */
public class ThreadLocal02 {

    //volatile static Person p = new Person();

    /*volatile*/ static ThreadLocal<Person> tl = new ThreadLocal<>();

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("睡两秒的" + tl.get());
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tl.set(new Person());

            System.out.println("睡1秒的===" + tl.get().name);

        }).start();

        /**
        * 运行后，观察控制台输出：
        * 睡1秒的===zhangsan
        * 睡两秒的null
        *
        * 两个线程使用同一个tl，但是睡1秒的线程创建了Person对象后，睡两秒的对象根本没有
        * 说明两个线程有各自的想法
        * 可以读一下ThreadLocal的set源码可以看出其中是维护了一个ThreadLocalMap
        *
        * https://www.cnblogs.com/dolphin0520/p/3920407.html
        */
    }
}
