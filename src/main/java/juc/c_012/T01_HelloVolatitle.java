package juc.c_012;

/**
 * volatile 关键字，使一个变量在多个线程间中可见
 * A B线程都用到一个变量，Java默认 A、B 线程线程保留一份copy，当 B 线程修改了该变量，A线程中未必知道
 *
 * 使用volatile关键字，会让所有线程都会读到变量的修改值
 *
 * 下面程序中，running变量存在于堆内存的 t 对象中，
 * 当线程t1开始执行时，会把running值从内存读到t1线程的工作区，在运行过程中，每次都使用这个copy的值，而不是每次都去读取内存中的值，
 * 这样当主线程改变了running的值，t1线程感知不到，所以不会停止运行。
 *
 * 使用volatile，会强制所有线程都去堆内存中读取running的值
 *
 * 可以读这篇文章加深理解：
 * http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
 *
 * volatile只能保证可见性，并不能保证多个线程共同修改running变量时带来的不一致问题，也就是说volatile不能替代synchronized。
 *
 * volatile 引用类型(包括数组)，只能保证引用本身的可见性，不能保证内部字段的可见性。
 *
 * @author wangwren
 */
public class T01_HelloVolatitle {

    /**
     * 在不加volatile情况下，这个程序会一直不停
     *
     * 加上volatile之后，执行完 m start... 1秒中之后就停了
     */
    private volatile boolean running = true;

    public void m(){
        System.out.println("m start...");

        //当running改变为false时，才会继续执行
        while (running){

        }

        System.out.println("m end...");
    }

    public static void main(String[] args) {

        T01_HelloVolatitle t = new T01_HelloVolatitle();

        new Thread(t::m,"t1").start();

        try {
            //主线程睡1秒才有效果，否则太快了，子线程刚执行，主线程就给running值改了，这时子线程读的running值就变成false了
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t.running = false;
    }
}
