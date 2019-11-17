package juc.c_033_FalseSharing;

/**
 * 解决伪共享问题
 * 观察该程序运行时间，明显比T01_CacheLinePadding快很多
 */
public class T02_CacheLinePadding {
    private static class Padding {
        //定义7个long类型值，但是不存任何值，只是占据内存中位置，7个就是56个字节
        public volatile long p1, p2, p3, p4, p5, p6, p7;
    }

    private static class T extends Padding {
        //T 继承了Padding，之后又定义了 x 也是long类型，那么加上 x 就有64个字节，正好是一个缓存行
        public volatile long x = 0L;
    }

    public static T[] arr = new T[2];

    static {
        //位置0是一个缓存行
        arr[0] = new T();
        //位置1是另一个缓存行，这样数组中数据就不在一个缓存行上了
        arr[1] = new T();
    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(()->{
            for (long i = 0; i < 1000_0000L; i++) {
                arr[0].x = i;
            }
        });

        Thread t2 = new Thread(()->{
            for (long i = 0; i < 1000_0000L; i++) {
                arr[1].x = i;
            }
        });

        final long start = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println((System.nanoTime() - start)/100_0000);
    }
}
