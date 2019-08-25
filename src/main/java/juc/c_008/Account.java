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
public class Account {

    private String name;

    private Integer balance = 0;

    /**
     * 转账，加锁；如果多个人转钱，高并发，那么可能钱就不对，加上锁之后，一个一个转
     */
    public synchronized void setBalance(String name,Integer balance){
        this.name = name;

        //在转账的时候需要耗时，这样才能有效果，这个锁是加在对象上，谁先获取到，就执行代码
        //因为主线程sleep了 1 秒，让转账的线程先执行，先获得了锁，转账过程中又睡了两秒钟
        //这时主线程又去查询，如果查询没有加锁，那么会出现脏读，因为转账操作没执行完毕。这也验证了，加锁方法和非加锁可以同时执行
        //但是，如果查询操作加了锁，那么在主线程睡 1 秒之后，做查询操作，这时转账操作还没完成，就查询不了，需要等待转账操作完成才能查询。
        //给查询操作加锁，消除了脏读；
        //查询操作是否需要加锁，看需求如何要求。
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.balance += balance;
    }

    /**
     * 查钱，查询的时候先不加锁
     * @return
     */
    public /*synchronized*/ Integer getBalance(String name){

        return this.balance;
    }

    public static void main(String[] args) {
        Account account = new Account();


        new Thread(() -> account.setBalance("张三",1000)).start();

        try {
            //主线程停了 1 秒
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account.getBalance("张三"));

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account.getBalance("张三"));


        //开启一个线程，转账


        /*for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println("转账===" + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //耗时操作应该放在转账方法内，这写外面了，转账操作是加了锁，方法执行太快，没有效果
                //而下面写的查询操作，直接就先读取，先获得了锁，读完锁释放，转账再拿锁
                account.setBalance("张三",1000);

                System.out.println("转账完成..." + Thread.currentThread().getName());
            }).start();
        }*/

        //再开一个线程，查钱
        /*new Thread(() -> {
            System.out.println(account.getBalance("张三"));

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(account.getBalance("张三"));
        }).start();*/
    }
}
