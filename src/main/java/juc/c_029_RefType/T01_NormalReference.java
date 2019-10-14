package juc.c_029_RefType;

import java.io.IOException;

/**
 * 强引用 特点：
 *      我们平常典型编码 Object obj = new Object();中的obj就是强引用。
 *      通过关键字new创建的对象所关联的引用就是强引用。当JVM内存空间不足，JVM宁愿抛出OOM运行时错误，
 *      使程序异常终止，也不会随意回收具有强引用的“存活”对象来解决内存不足的问题。
 *      对于一个普通对象，如果没有其他引用关系，只要超过了引用的作用域或者显示地将相应(强)引用赋值为null，
 *      就是可以被垃圾收集了，具体回收时机要看垃圾收集策略。
 *
 * @author wangwren
 */
public class T01_NormalReference {

    public static void main(String[] args) throws IOException {

        M m = new M();

        m = null;

        //垃圾回收
        System.gc();

        //阻塞住
        System.in.read();
    }
}
