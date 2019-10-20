package juc.c_032_ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 普通方法用时：2032
 * 多线程用时：527
 */
public class T09_FixedThreadPool {

    public static void main(String[] args) {

        //普通找法
        long start = System.currentTimeMillis();
        getPrime(1,200000);
        long end = System.currentTimeMillis();

        System.out.println("普通方法用时：" + (end - start));

        //线程池，多线程执行
        ExecutorService service = Executors.newFixedThreadPool(4);

        MyTask task1 = new MyTask(1,80000);
        MyTask task2 = new MyTask(80001,130000);
        MyTask task3 = new MyTask(130001,170000);
        MyTask task4 = new MyTask(170001,200000);

        Future<List<Integer>> future1 = service.submit(task1);
        Future<List<Integer>> future2 = service.submit(task2);
        Future<List<Integer>> future3 = service.submit(task3);
        Future<List<Integer>> future4 = service.submit(task4);

        start = System.currentTimeMillis();

        try {
            future1.get();
            future2.get();
            future3.get();
            future4.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        service.shutdown();

        end = System.currentTimeMillis();

        System.out.println("多线程用时：" + (end - start));
    }


    /**
     * 找质数
     */
    static class MyTask implements Callable<List<Integer>>{

        private int startPos,endPos;

        public MyTask(int startPos,int endPos){
            this.startPos = startPos;
            this.endPos = endPos;
        }

        @Override
        public List<Integer> call() throws Exception {

            List<Integer> list = getPrime(startPos,endPos);

            return list;
        }

    }

    static boolean isPrime(int num){
        for (int i = 2; i < num / 2; i++) {
            if (num % i == 0){
                return false;
            }
        }
        return true;
    }


    static List<Integer> getPrime(int start,int end){
        List<Integer> results = new ArrayList<>();
        for (int i = start; i < end; i++) {
            if (isPrime(i)){
                results.add(i);
            }
        }

        return results;
    }
}
