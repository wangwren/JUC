package juc.c_012;

/**
 * volatile 引用类型(包括数组)，只能保证引用本身的可见性，不能保证内部字段的可见性。
 * @author wangwren
 */
public class VolatileReference {

    public static class Data{
        private int a;
        private int b;

        public Data(int a,int b){
            this.a = a;
            this.b = b;
        }
    }

    volatile static Data data;

    public static void main(String[] args) {
        Thread write = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                //data一直在变，在创建新的对象
                data = new Data(i,i);
            }
        });

        Thread read = new Thread(() -> {
           while (data == null){
               //如果data等于 null ，就一直循环
           }

           int x = data.a;
           //运行后a，b值不同，a一直是0，b是write线程运行了一段时间后的值了，因为data一直在变
           int y = data.b;

           if (x != y){
               System.out.printf("a = %s, b= %s%n", x, y);
           }
        });


        write.start();
        read.start();

        try {
            read.join();
            write.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("end...");
    }
}
