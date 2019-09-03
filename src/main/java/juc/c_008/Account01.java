package juc.c_008;

import java.util.concurrent.TimeUnit;

/**
 * 面试题：模拟银行账户
 * 对业务写方法加锁
 * 对业务读方法不加锁
 * 这样行不行？
 *
 * 容易产生脏读问题（dirtyRead）
 *
 * @author wangwren
 */
public class Account01 {

    private String username;

    private int balance = 0;

    /**
    * 转账操作
    */
    public synchronized void setBalance(String username,int balance){
        System.out.println("转账....");
        this.username = username;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.balance += balance;
    }

    /**
    * 功能描述:读操作，读操作加锁后，就可以防止脏读
    */
    public synchronized int getBalance(String username){
        System.out.println("查询");
        return this.balance;
    }

    public static void main(String[] args) {
        Account01 account = new Account01();

        new Thread(() -> {
            account.setBalance("张三",100);
        }).start();

        //这里让主线程睡一秒，让转账操作先执行。
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account.getBalance("张三"));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account.getBalance("张三"));

    }
}
