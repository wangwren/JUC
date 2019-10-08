package juc.c_028_interview.A1B2C3;

import java.util.concurrent.locks.Lock;

/**
 * @author: wangwren
 * @date: 2019/10/8
 * @description: 使用wait/notify实现A1B2C3
 * @version: 1.0
 */
public class WaitNotify {



    public static void main(String[] args) {
        final Object lock = new Object();

        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < nums.length; i++) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.print(nums[i]);

                    lock.notify();

                }
            }
        }).start();


        new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < letters.length; i++) {
                    System.out.print(letters[i]);

                    lock.notify();

                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();



    }
    
}
