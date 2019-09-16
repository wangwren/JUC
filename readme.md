# 高并发与多线程

## 线程
- 线程的创建和启动
- 线程的sleep、yield、join
- 线程的状态

代码在 **c_000** 部分。

## synchronized关键字(悲观锁)

- synchronized(Object)
    - 不能用String常量、Integer、Long。
    - 锁住的是对象
    - 代码 **c_014** 部分。

- 线程同步
    - synchronized锁的是对象，不是代码。
    - **锁定方法和非锁定方法可以同步进行**
    
- synchronized优化：代码 **c_013** 部分

synchronized底层实现：
- 早期JDK中，synchronized是重量级的，即需要调用操作系统(OS)来申请锁。
- 后来改进了，有了锁的升级：
    - Java虚拟机中并没有严格规定synchronized需要如何实现，只要能满足锁住一个对象，一个一个线程的去执行其中的代码块即可。
    - sync(Object)锁住一个对象，这时会markWord，记录这个线程的ID，这时只有一个线程来，其实是没有锁住的，这时是**偏向锁**。即就一个线程，我偏向你。
    - 当有锁的争用时：升级为**自旋锁**。即另一个线程会在CPU内打转，转圈等着，**自旋10次**，10次之内拿到锁，就拿到了；拿不到就升级，去排队。
    - 自旋10次之后，升级为重量级锁，OS，需要操作系统了。
    
- 只有锁的升级，没有锁的降级。

### 那什么时候用自旋，什么时候用重量级锁？
- 执行时间少(加锁代码)，线程数少(如果线程数太多了，用自旋也不行)，用自旋锁；因为每个线程会很快的执行完了。
- 执行时间长，线程数多，用系统(OS)锁。重量级锁。

synchronized代码在 **c_001** 至 **c_011** 部分。

## volatile关键字
- 保证线程间可见
    - MESI
    - 缓存一致性协议
    
- 禁止指令重排
    - DCL单例
    - Double Check Lock
    
可参考内容[Java内存模型](http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html)

volatile代码在 **c_012** 部分  
[单例模式--双检锁代码](https://github.com/wangwren/DesignPatterns/blob/master/src/main/java/com/wangwren/singleton/Singleton05.java)


## CAS (无锁优化 乐观、自旋)
**原子操作**

- Compare And Set (CAS)

- 操作原理

```
//V：代表要改的那个值；E:代表期望值；N:代表新值
//只有目前的变量值和所期望的值相等的时候，才会去赋值新值；否则再次尝试或失败
cas(V,E,N){
    if(V == E){
        V = N;
    }
    //otherwise try again or fail
}
```

- CAS 的 ABA 问题

即期望值一开始是A，由于别人操作改成了B，之后又变成了A。
解决方式：加version(版本)
- A:1.0
- B:2.0
- A:3.0
- cas(version)

**如果是基础类型(int...)，无所谓；如果是引用类型，比如你和你的前女友复合，你不知道她中间经历了多少男人或女人，你不难受吗**。

Java的CAS操作，AtomicXXX类，都依靠了Unsafe类。这个类很牛逼，可以像C和C++一样操作内存，但是该类在JDK1.9之后不让用了。

代码在**c_015**部分。

## ReentrantLock 重入锁
- ReentrantLock可用来替代synchronized，使用ReentrantLock可以完成同样的功能。
- 注意：使用ReentrantLock**必须要手动释放锁**
- 使用synchronized时，遇到异常，jvm会自动释放锁；但是使用ReentrantLock需要手动释放，所以经常将锁的释放写在finally中。
- 使用reentrantlock可以进行“尝试锁定”tryLock，这样无法锁定，或者在指定时间内无法锁定，线程可以决定是否继续等待。
- 使用ReentrantLock还可以调用lockInterruptibly方法，可以对线程interrupt方法做出响应，在一个线程等待锁的过程中，可以被打断。
- ReentrantLock还可以指定为公平锁。

代码在**c_016**部分

## CountDownLatch 门闩

- 应用场景，可以等待其他线程都执行完毕后再继续执行别的操作。使用join方法也可以实现，但是join方法不太好控制。
- 真是场景中，我使用过CountDownLatch。开启了十个线程去查询数据，等十个线程都操作完毕后，进行打包下载。