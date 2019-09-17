package juc.c_019;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 阶段。相当于有多个栅栏，但是多个线程中，有的线程就可以通过该阶段，进入下一阶段；
 * 但是有的线程只能卡在当前阶段，不允许继续下一阶段。
 *
 * 下面程序模拟结婚的流程。
 *
 * 人员入场，所有人都到齐  -->  吃饭，所有人都吃饭  --> 离场，所有人都走了  --> 新郎新娘抱抱，这时候就只有新郎新娘，不能是所有人了
 * @author: wangwren
 */
public class T01_TestPhaser {
    static Random random = new Random();
    static MarriagerPhaser phaser = new MarriagerPhaser();

    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //一共多少个人
        phaser.bulkRegister(7);

        for (int i = 0; i < 5; i++) {
            //创建5个线程代表来宾
            new Thread(new Person("p" + i)).start();
        }

        //两个主角
        new Thread(new Person("新郎")).start();
        new Thread(new Person("新娘")).start();
    }


    /**
    * 继承Phaser类，重写onAdvance方法
    */
    static class MarriagerPhaser extends Phaser{
        /**
        * 该方法会被自动调用
        * phaser 表示现在在第几阶段，通过方法的调用顺序来表示第几阶段，从0开始
        * registeredParties 进入该阶段的线程个数，即并不是所有的线程都能进入某一阶段
        */
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {

            switch (phase){
                //第一阶段
                case 0 :
                    System.out.println("所有人都到齐了！" + registeredParties);
                    System.out.println();
                    //返回false，表示该阶段已经结束了，而不是所有阶段都结束了
                    //如果在这里改为true，那么onAdvance调用一次后，将不会再被调用了，因为所有都完成了
                    return false;

                //第二阶段
                case 1 :
                    System.out.println("所有人都吃完了！" + registeredParties);
                    System.out.println();
                    return false;

                //第三阶段
                case 2 :
                    System.out.println("所有人都离场了!" + registeredParties);
                    System.out.println();
                    return false;

                //第四阶段
                case 3 :
                    System.out.println("婚礼结束，新郎新娘抱抱！" + registeredParties);
                    System.out.println();
                    //最后一个阶段完成，返回true，所有阶段都完成
                    return true;

                default:
                    return true;
            }
        }
    }


    static class Person implements Runnable{
        private String name;
        public Person(String name){
            this.name = name;
        }

        public void arrive(){
            milliSleep(random.nextInt(1000));
            System.out.printf("%s 到达现场！\n",name);

            //进入下一阶段。到达并等待进入下一阶段，当所有要进入下一阶段的线程都到了时，就会推翻当前阶段
            //当前线程可以进入下一阶段，别的线程能不能进入下一阶段跟当前线程也没关系
            phaser.arriveAndAwaitAdvance();
        }

        public void eat(){
            milliSleep(random.nextInt(1000));
            System.out.printf("%s 吃完！\n",name);

            phaser.arriveAndAwaitAdvance();
        }

        public void leave(){
            milliSleep(random.nextInt(1000));
            System.out.printf("%s 离开！\n",name);

            phaser.arriveAndAwaitAdvance();
        }

        public void hug(){
            if ("新郎".equals(name) || "新娘".equals(name)){
                milliSleep(random.nextInt(1000));
                System.out.printf("%s 洞房！\n",name);
                //如果是新郎或新娘，允许继续进行下一阶段
                phaser.arriveAndAwaitAdvance();
            } else {
                //如果是别人，那么虽然已经到达下一阶段，但是不允许你注册了，你就被踢出去了
                phaser.arriveAndDeregister();

                //但是也可以被注册回来,打开下面注释，程序会卡在最后一个阶段
                //phaser.register();
            }
        }


        @Override
        public void run() {
            //通过方法的调用顺序来指定是第几个阶段
            arrive();

            eat();

            //改变方法的调用顺序，就发现确实某一阶段是与方法调用顺序相关的
            //arrive();

            leave();

            hug();
        }
    }
}
