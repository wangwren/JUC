package juc.c_026_VarHandle;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * VarHandle是JDK1.9才有的
 *
 * 注意pom.xml文件中
 * <maven.compiler.source>1.11</maven.compiler.source>
 * <maven.compiler.target>1.11</maven.compiler.target>
 * 这两处需要改成1.11版本，否则编译提示找不到符号
 *
 * @author wangwren
 */
public class T01_HelloVarHandle {

    int x = 8;

    private static VarHandle handle;

    static {
        try {
            //固定写法，将handle指向了T01_HelloVarHandle类中名字叫做 x， 类型为int的变量
            //这时handle也是 x 的引用
            handle = MethodHandles.lookup().findVarHandle(T01_HelloVarHandle.class,"x",int.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        T01_HelloVarHandle t = new T01_HelloVarHandle();

        //打印x的值
        System.out.println(handle.get(t));

        //设置x的值,将x的值改为9了
        handle.set(t,9);

        System.out.println(t.x);

        //VarHandle还可以做CAS操作,预期是9，改为10
        handle.compareAndSet(t,9,10);

        //CAS操作,将x加10
        handle.getAndAdd(t,10);

        System.out.println(t.x);
    }
}
