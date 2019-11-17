package juc.c_033_FalseSharing;

/**
 * 缓存行，伪共享
 */
public class T01_CacheLinePadding {

    private static class T {
        //一个long占8个字节
        public volatile long x = 0L;
    }

    //定义一个数组
    public static T[] arr = new T[2];

    static {
        arr[0] = new T();
        arr[1] = new T();
    }

    public static void main(String[] args) throws Exception {
        //t1 只修改数组0位置上的值
        Thread t1 = new Thread(()->{
            for (long i = 0; i < 1000_0000L; i++) {
                arr[0].x = i;
            }
        });

        //t2 只修改数组1位置上的值
        Thread t2 = new Thread(()->{
            for (long i = 0; i < 1000_0000L; i++) {
                arr[1].x = i;
            }
        });

        //因为有缓存行的原因，t1，t2读的时候都会一下把数组都读出来，哪怕t1不需要修改位置1的值；t2不需要修改0位置的值
        //这样每次读完数据，改后，t1,t2都需要重新读一下缓存行，哪怕他们不需要被修改的值，因为他们只关心对应的位置。

        final long start = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println((System.nanoTime() - start)/100_0000);
    }
}
