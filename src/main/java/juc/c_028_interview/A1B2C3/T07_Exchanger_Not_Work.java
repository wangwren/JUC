package juc.c_028_interview.A1B2C3;

import java.util.concurrent.Exchanger;

/**
 * 使用Exchanger方式，两个线程间的通信，但是实现不了
 *
 * 因为exchange后，两个线程就不会阻塞了，谁先运行都不确定
 */
public class T07_Exchanger_Not_Work {

    private static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        new Thread(() -> {
            for (int i = 0; i < letters.length; i++) {
                System.out.print(letters[i]);

                try {
                    exchanger.exchange("T1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        new Thread(() -> {

            for (int i = 0; i < nums.length; i++) {
                try {
                    exchanger.exchange("T2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.print(nums[i]);
            }
        }).start();
    }
}
