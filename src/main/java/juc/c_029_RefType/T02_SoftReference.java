package juc.c_029_RefType;

import java.lang.ref.SoftReference;

/**
 * 软引用
 *      软引用是用来描述一些还有用但并非必须的对象。
 *      对于软引用关联着的对象，在系统将要发生内存溢出异常之前，将会把这些对象列进回收范围进行第二次回收。
 *      如果这次回收还没有足够的内存，才会抛出内存溢出异常
 *
 * 下面这个程序需要先设置虚拟机最大和最小内存为20M，-Xms20M -Xmx20M
 * 这样在创建软引用时消耗了10M，之后又创建了一个数组消耗15M，这样导致超过最大内存
 * 所以在第三次获取软引用数据的时候，就是null了。
 *
 *
 * 软引用非常适合做缓存使用。
 *
 * @author: wangwren
 */
public class T02_SoftReference {

    public static void main(String[] args) {

        //开辟了10M内存
        SoftReference<byte[]> soft = new SoftReference<>(new byte[1024*1024*10]);

        System.out.println("first get =====" + soft.get());

        System.gc();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("second get =====" + soft.get());

        //再开辟一个数组，分配15M内存，设定JVM内存最大为20M
        //heap装不下了，这时系统会垃圾回收，先回收一次，如果不够，会把软引用干掉。
        byte[] b = new byte[1024*1024*15];

        System.out.println("thrid get ====" + soft.get());
    }
}
