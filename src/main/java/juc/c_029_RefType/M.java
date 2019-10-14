package juc.c_029_RefType;

/**
 * @author: wangwren
 * @date: 2019/10/14
 * @description: juc.c_029_RefType
 * @version: 1.0
 */
public class M {

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize...");
    }
}
