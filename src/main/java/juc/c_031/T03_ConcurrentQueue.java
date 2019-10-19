package juc.c_031;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class T03_ConcurrentQueue {
    public static void main(String[] args) {
        Queue<String> queue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < 10; i++) {
            //offer方法和add方法一样，都是添加元素，只是add方法在容器满时添加元素会报错
            queue.offer("a" + i);
        }

        System.out.println(queue);

        System.out.println("size===" + queue.size());

        //poll后，取出元素并移除元素
        System.out.println("poll===" + queue.poll());
        System.out.println("size===" + queue.size());

        //peek后，取出元素，但不会移除该元素
        System.out.println("peek===" + queue.peek());
        System.out.println("size===" + queue.size());
    }
}
