package juc.c_000;

/**
 * 线程状态
 * @author: wangwren
 */
public class T04_ThreadState {

    static class MyThread extends Thread{
        @Override
        public void run() {
            //获取线程状态，此时的t线程已经启动运行了
            System.out.println(this.getState());

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(i);
            }
        }
    }

    public static void main(String[] args) {

        MyThread t = new MyThread();

        //此时的t线程还没有启动，看一下状态
        System.out.println(t.getState());

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //线程结束时的状态
        System.out.println(t.getState());
    }
}
