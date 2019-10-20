package juc.c_028_interview.A1B2C3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 使用BlockingQueue实现，BlockingQueue本身就带阻塞
 *
 * 这里的实现思路是定义两个BlockingQueue，大小都定义为1
 *
 * 第一个线程中往queue2中put ok值，之后queue1.take等待取出，这时queue1中没有put，所以等待
 *
 * 第二个线程先take阻塞，第一个线程已经放入ok值了，所以第二个线程会运行，take后queue2就空了，之后放入queue1值，导致线程一不阻塞了
 * 第二个线程循环继续，queue2.take，因为queue2已经取出值，导致queue2空，线程二等待。
 */
public class T06_BlockingQueue {

    static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(1);
    static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(1);

    public static void main(String[] args) {
        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        new Thread(() -> {
            for (int i = 0; i < letters.length; i++) {
                System.out.print(letters[i]);
                try {
                    queue2.put("ok");
                    queue1.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        new Thread(() -> {
            for (int i = 0; i < nums.length; i++) {
                try {
                    queue2.take();
                    System.out.print(nums[i]);
                    queue1.put("ok");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
