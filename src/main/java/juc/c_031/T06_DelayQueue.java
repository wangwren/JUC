package juc.c_031;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class T06_DelayQueue {

    //DelayQueue中的泛型必须要实现Delayed
    static BlockingQueue<MyDelayed> queue = new DelayQueue<MyDelayed>();

    static class MyDelayed implements Delayed{

        private String name;
        //运行时间，用作比较用
        private long runningTime;

        public MyDelayed(String name,long runningTime){
            this.name = name;
            this.runningTime = runningTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(runningTime - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        }

        /**
         * 定义排序的规则
         * @param o
         * @return
         */
        @Override
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)){
                return -1;
            } else if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)){
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "MyDelayed{" +
                    "name='" + name + '\'' +
                    ", runningTime=" + runningTime +
                    '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long now = System.currentTimeMillis();

        MyDelayed m1 = new MyDelayed("m1",now + 1000);
        MyDelayed m2 = new MyDelayed("m2",now + 2000);
        MyDelayed m3 = new MyDelayed("m3",now + 1500);
        MyDelayed m4 = new MyDelayed("m4",now + 2500);
        MyDelayed m5 = new MyDelayed("m5",now + 500);


        queue.put(m1);
        queue.put(m2);
        queue.put(m3);
        queue.put(m4);
        queue.put(m5);

        System.out.println(queue);

        //输出结果是按指定时间由小到大输出
        //这里的判断不要使用 queue.size 因为take会移除元素，这样就导致size一直变，输出的结果不完整
        for (int i = 0; i < 5; i++) {
            System.out.println(queue.take());
        }
    }
}
