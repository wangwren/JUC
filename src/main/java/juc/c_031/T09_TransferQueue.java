package juc.c_031;

import java.util.concurrent.LinkedTransferQueue;

public class T09_TransferQueue {
    public static void main(String[] args) throws InterruptedException {
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

        /*new Thread(() -> {
            try {
                System.out.println(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();*/

        //queue.transfer("aaa");

        //使用put，注释掉上面代码，程序会结束，而使用transfer不会，程序会阻塞等待
        queue.put("bbb");
    }
}
