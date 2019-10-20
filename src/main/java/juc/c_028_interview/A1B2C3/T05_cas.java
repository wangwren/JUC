package juc.c_028_interview.A1B2C3;


/**
 * 使用类似cas的操作实现，不依靠锁
 *
 * 使用while死循环一直占用CPU资源，但是使用锁的wait是不占用锁的
 */
public class T05_cas {

    //使用枚举类型更加严谨，当然使用Boolean类型也可以
    enum ReadyToRun{T1,T2};

    //指定先运行T1，这里加上volatile保证线程间可见
    static volatile ReadyToRun r = ReadyToRun.T1;

    public static void main(String[] args) {

        char[] letters = "ABCDEFG".toCharArray();
        char[] nums = "1234567".toCharArray();

        new Thread(() -> {

            for (int i = 0; i < letters.length; i++) {

                //如果不是T1，就死循环卡在这
                while (r != ReadyToRun.T1){}

                //如果是则继续运行
                System.out.print(letters[i]);

                //指定T2
                r = ReadyToRun.T2;
            }
        }).start();


        new Thread(() -> {

            for (int i = 0; i < nums.length; i++) {

                //如果不是T2，就死循环卡在这
                while (r != ReadyToRun.T2){}

                //如果是则继续运行
                System.out.print(nums[i]);

                //指定T1
                r = ReadyToRun.T1;
            }
        }).start();
    }
}
