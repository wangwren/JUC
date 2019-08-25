package juc.c_002;

/**
 * synchronized 关键字
 * 锁定this
 * @author: wangwren
 */
public class T {
    private int count = 100;

    public void m(){
        synchronized (this){
            //锁定当前对象this，任何线程想要执行下面的代码，必须先拿到this的锁
            count--;
            System.out.println(Thread.currentThread().getName() + " count = " + count);
        }
    }

    public static void main(String[] args) {
        T t = new T();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                t.m();
            }).start();
        }
    }
}
