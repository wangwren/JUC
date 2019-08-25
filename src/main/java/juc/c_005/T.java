package juc.c_005;

/**
 * 分析一下该程序的输出
 *
 * 在不加sychronized的情况下输出的结果是乱的，不是99 98 97这种有序的
 *
 * synchronized 可以保证可见性和原子性
 * 而volatile 只能保证可见性
 *
 * @author wangwren
 */
public class T implements Runnable {

    private /*volatile*/ int count = 100;

    @Override
    public /*synchronized*/ void run() {
        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);
    }

    public static void main(String[] args) {
        T t = new T();

        for (int i = 0; i < 100; i++) {
            new Thread(t,"THREAD" + i).start();
        }
    }
}
