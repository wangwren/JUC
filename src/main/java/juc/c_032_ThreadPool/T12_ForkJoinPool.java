package juc.c_032_ThreadPool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 *
 * 要计算0至一亿的和，自己先使用fork/join实现一下
 *
 *
 *  RecursiveTask是带有返回值的，RecursiveAction不带有返回值
 *  Recursive的意思是“递归”
 *
 * @author wangwren
 */
public class T12_ForkJoinPool {


    public static void main(String[] args) {

        //使用ForkJoin

        long startTime = System.currentTimeMillis();

        //创建一个Fork/Join池
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinCalcuate(0,10000000000L);

        //执行任务
        Long sum = pool.invoke(task);
        System.out.println("fork/join:" + sum);
        long endTime = System.currentTimeMillis();
        System.out.println("fork/join耗费时间：" + (endTime - startTime));



        //使用普通for循环
        startTime = System.currentTimeMillis();

        sum = 0L;
        for (long i = 0 ; i <= 10000000000L ; i ++ ){
            sum += i;
        }

        System.out.println("for:" + sum);
        endTime = System.currentTimeMillis();
        System.out.println("for消耗时间：" + (endTime - startTime));


        //使用java8提供的并行流
        startTime = System.currentTimeMillis();

        long sum1 = LongStream.rangeClosed(0, 10000000000L)
                .parallel()
                .sum();

        System.out.println("parallel:" + sum1);

        endTime = System.currentTimeMillis();

        System.out.println("并行流消耗时间：" + (endTime - startTime));
    }


    /**
     * 定义拆分规则
     */
    static class ForkJoinCalcuate extends RecursiveTask<Long>{

        private long start;
        private long end;

        //临界值，设为一万，即小任务中包含一万个数
        private static final long THRESHOLD = 10000L;
        public ForkJoinCalcuate(long start,long end){
            this.start = start;
            this.end = end;
        }


        @Override
        protected Long compute() {

            long length = end -start;
            if (length < THRESHOLD){

                long sum = 0;

                //如果指定的数小于10000，则正常循环从start加到end即可
                for (long i = start; i <= end; i++) {
                    sum += start;
                }

                return sum;
            } else {
                //大于10000的情况
                long mid = (end + start) / 2;

                //将任务拆分
                ForkJoinCalcuate left = new ForkJoinCalcuate(start,mid);
                ForkJoinCalcuate right = new ForkJoinCalcuate(mid + 1,end);

                //压入线程队列，fork是ForkJoinTask提供的方法
                left.fork();
                right.fork();

                //合并
                return left.join() + right.join();

            }
        }
    }
}
