package juc.c_032_ThreadPool;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 这个类CompletableFuture很强大，其内部实现很麻烦，使用的是ForkJoinPool
 *
 * 可以管理多个Future的结果，而这只是他的其中一个功能
 *
 *
 * 假设你能提供一个服务，这个服务查询各大电商网站同一类产品的价格并汇总展示
 */
public class T05_CompletableFuture {

    public static void main(String[] args) {

        long start,end;

        start = System.currentTimeMillis();

        /*//如果正常写，那么依次调用下面的三个方法就可以了
        priceOfTM();
        priceOfTB();
        priceOfJD();

        end  = System.currentTimeMillis();

        //但是这是串行的，很耗时啊
        System.out.println("用时：" + (end - start));*/

        //使用CompletableFuture

        //supplyAsync会异步的执行
        /*CompletableFuture<Double> futureTM = CompletableFuture.supplyAsync(() -> priceOfTM());
        CompletableFuture<Double> futureTB = CompletableFuture.supplyAsync(() -> priceOfTB());
        CompletableFuture<Double> futureJD = CompletableFuture.supplyAsync(() -> priceOfJD());

        //合并这三个任务，三个任务都执行完
        CompletableFuture.allOf(futureTM,futureTB,futureJD).join();*/


        //也可以使用lambda一下全写完
        CompletableFuture.supplyAsync(() -> priceOfTM())
                .thenApply(String::valueOf)
                .thenApply(str -> "price " + str)
                .thenAccept(System.out::println);


        CompletableFuture.supplyAsync(() -> priceOfTB())
                .thenApply(String::valueOf)
                .thenApply(str -> "price " + str)
                .thenAccept(System.out::println);


        CompletableFuture.supplyAsync(() -> priceOfJD())
                .thenApply(String::valueOf)
                .thenApply(str -> "price " + str)
                .thenAccept(System.out::println);





        end  = System.currentTimeMillis();

        System.out.println("用时：" + (end -start));

        try {
            //阻塞一下，都是异步的
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 假设从天猫获取
     * @return
     */
    private static double priceOfTM(){
        delay();

        return 1.00;
    }

    /**
     * 假设从淘宝获取数据
     * @return
     */
    private static double priceOfTB(){
        delay();

        return 2.00;
    }

    private static double priceOfJD(){
        delay();

        return 3.00;
    }


    /**
     * 模拟抓取数据的过程，只是随机的睡了几秒
     */
    private static void delay(){
        int time = new Random().nextInt(500);

        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("After %s sleep! \n",time);
    }
}
