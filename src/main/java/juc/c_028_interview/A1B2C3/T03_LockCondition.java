package juc.c_028_interview.A1B2C3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: wangwren
 * @date: 2019/10/8
 * @description: 使用lockCondition解决
 * @version: 1.0
 */
public class T03_LockCondition {
    char[] letters = "ABCDEFG".toCharArray();
    char[] nums = "1234567".toCharArray();

    Lock lock = new ReentrantLock();
    Condition letCon = lock.newCondition();
    Condition numsCon = lock.newCondition();

    public static void main(String[] args) {


        T03_LockCondition lc = new T03_LockCondition();

        new Thread(() -> {
            lc.printNums();
        }).start();

        new Thread(() -> {
            lc.printLet();
        }).start();

    }


    public void printLet(){
        try {
            lock.lock();
            for (int i = 0; i < letters.length; i++) {
                System.out.print(letters[i]);
                numsCon.signalAll();
                letCon.await();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printNums(){
        try {
            lock.lock();
            for (int i = 0; i < nums.length; i++) {
                numsCon.await();
                System.out.print(nums[i]);
                letCon.signalAll();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
