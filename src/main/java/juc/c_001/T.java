package juc.c_001;

/**
 * synchronized 关键字
 * 对某个对象加锁
 * @author: wangwren
 */
public class T {

    private int count = 10;

    private Object o = new Object();

    public void m(){
        synchronized (o){
            //只有每个对象拿到了 o 这把锁才会执行下面这段代码
            count--;
            System.out.println(Thread.currentThread().getName() + " count:" + count);
        }
    }

    public static void main(String[] args) {
        //这里需要保证 t 是同一个对象，如果不加锁那么会造成线程不安全
        T t = new T();

        for (int i = 0; i < 10; i++){
            new Thread(() -> {
                t.m();
            }).start();
        }
    }
}
