package juc.c_028_interview.A1B2C3;

import java.util.concurrent.locks.LockSupport;

/**
 * 使用LockSupport解决，LockSupport更实现的更简单
 */
public class T04_LockSupport {

    static Thread t1 = null;
    static Thread t2 = null;

    public static void main(String[] args) {
        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        t1 = new Thread(() -> {
            for (int i = 0; i < letters.length; i++) {
                System.out.print(letters[i]);
                LockSupport.unpark(t2); //叫醒t2
                LockSupport.park(); //阻塞t1
            }
        });

        t2 = new Thread(() -> {
            for (int i = 0; i < nums.length; i++) {
                LockSupport.park(); //阻塞t2
                System.out.print(nums[i]);
                LockSupport.unpark(t1); //叫醒t1
            }
        });

        t1.start();
        t2.start();
    }
}
