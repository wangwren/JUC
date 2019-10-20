package juc.c_032_ThreadPool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class T07_SingleThreadExecutor {

    public static void main(String[] args) {

        ExecutorService service = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            //execute方法没有返回值，submit方法又返回值
            service.execute(() -> {
                System.out.println(finalI + " " + Thread.currentThread().getName());
            });
        }

        service.shutdown();
    }
}
