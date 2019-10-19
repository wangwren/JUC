package juc.c_031;

import java.util.PriorityQueue;

/**
 * 默认就是有排序的，内部有一个二叉堆
 */
public class T07_PriorityQueue {

    public static void main(String[] args) {

        PriorityQueue<String> queue = new PriorityQueue<>();

        queue.add("c");
        queue.add("e");
        queue.add("a");
        queue.add("d");
        queue.add("z");

        for (int i = 0; i < 5; i++) {
            //输出 a c d e z
            System.out.println(queue.poll());
        }
    }
}
