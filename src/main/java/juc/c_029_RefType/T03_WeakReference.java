package juc.c_029_RefType;

import java.lang.ref.WeakReference;

/**
 * 弱引用
 *      弱引用遇到gc就会被垃圾回收
 *
 * ThreadLocal中的threadLocals的ThreadLocalMap中的Entity就是继承的弱引用
 *
 * WeakHashMap 中的entity也是继承的弱引用
 *
 * @author: wangwren
 */
public class T03_WeakReference {


    public static void main(String[] args) {
        WeakReference<M> weak = new WeakReference<>(new M());

        System.out.println(weak.get());

        System.gc();

        System.out.println(weak.get());
    }

}
