package juc.c_028_interview.A1B2C3;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

public class T08_TransferQueue {

    static TransferQueue<String> queue = new LinkedTransferQueue<>();

    public static void main(String[] args) {
        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        new Thread(() -> {
            for (int i = 0; i < letters.length; i++) {
                try {
                    System.out.print(queue.take());
                    //往第二个线程中放入数字
                    queue.transfer(String.valueOf(nums[i]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        new Thread(() -> {
            for (int i = 0; i < nums.length; i++) {
                try {
                    //往第一个线程中放入字母，交给他打印
                    queue.transfer(String.valueOf(letters[i]));
                    System.out.print(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
