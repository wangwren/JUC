package juc.c_007;

/**
 * 同步和非同步方法是否可以同时调用
 *
 * 即m1方法加锁，m2方法不加锁，在同时调用m1、m2方法时，是否会影响m2
 *
 * 通过输出结果可以看出，在m1执行时，m2方法也进行了输出
 *
 * @author wangwren
 */
public class T {

    /**
     * 加锁方法
     */
    public synchronized void m1(){
        System.out.println(Thread.currentThread().getName() + "m1 start...");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "m1 end...");
    }

    /**
     * 不加锁方法
     */
    public void m2(){

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "m2");
    }

    public static void main(String[] args) {

        T t = new T();

        new Thread(t::m1).start();
        new Thread(t::m2).start();
    }
}
