# 高并发与多线程

## 线程
- 线程的创建和启动
- 线程的sleep、yield、join
- 线程的状态

代码在 **[c_000](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_000)** 部分。

## synchronized关键字(**悲观锁**)

- synchronized(Object)
    - 不能用String常量、Integer、Long。
    - 锁住的是对象
    - 代码 **[c_014](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_014)** 部分。

- 线程同步
    - synchronized锁的是对象，不是代码。
    - **锁定方法和非锁定方法可以同步进行**
    
- synchronized优化：代码 **[c_013](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_013)** 部分

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

synchronized代码在 **[c_001](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_001)** 至 **[c_011](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_011)** 部分。

## volatile关键字
- 保证线程间可见
    - MESI
    - 缓存一致性协议
    
- 禁止指令重排
    - DCL单例
    - Double Check Lock
    
可参考内容[Java内存模型](http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html)

volatile代码在 **[c_012](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_012)** 部分  
[单例模式--双检锁代码](https://github.com/wangwren/DesignPatterns/blob/master/src/main/java/com/wangwren/singleton/Singleton05.java)


## CAS (无锁优化 **乐观锁、自旋锁**)
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

- 分段锁
LongAdder类

代码在**[c_015](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_015)**部分。

## ReentrantLock 重入锁
- ReentrantLock可用来替代synchronized，使用ReentrantLock可以完成同样的功能。
- 注意：使用ReentrantLock**必须要手动释放锁**
- 使用synchronized时，遇到异常，jvm会自动释放锁；但是使用ReentrantLock需要手动释放，所以经常将锁的释放写在finally中。
- 使用reentrantlock可以进行“尝试锁定”tryLock，这样无法锁定，或者在指定时间内无法锁定，线程可以决定是否继续等待。
- 使用ReentrantLock还可以调用lockInterruptibly方法，可以对线程interrupt方法做出响应，在一个线程等待锁的过程中，可以被打断。
- ReentrantLock还可以指定为公平锁。

### synchronized和ReentrantLock有什么不同？
- synchronized是系统自带，可以自动加锁，自动解锁；ReentrantLock需要手动加锁，手动解锁。
- ReentrantLock可以出现各种各样的等待队列。synchronized做不到这一点。
- 底层实现：
    - ReentrantLock底层是CAS的实现；
    - synchronized默认进行了四种锁的状态的升级。

代码在**[c_016](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_016)**部分

## CountDownLatch 门闩

- 应用场景，可以等待其他线程都执行完毕后再继续执行别的操作。使用join方法也可以实现，但是join方法不太好控制。
- 真是场景中，我使用过CountDownLatch。开启了十个线程去查询数据，等十个线程都操作完毕后，进行打包下载。

**countDown也不是说只能在一个线程里countDown一下，也可以在一个线程里countDown N多下**，只要到0了，就继续执行剩下代码。

代码**[c_017](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_017)**部分

## CyclicBarrier(栅栏)

代码**[c_018](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_018)**部分

## Phaser(阶段)

代码**[c_019](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_019)**部分

## ReadWriteLock 读写锁

- **共享锁**，读锁就是共享锁，在读的时候大大提高效率。即只要是读操作，就不会阻塞等待锁释放，大家可以一起读。

- **排他锁，互斥锁**，写锁就是排他锁

代码**[c_020](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_020)**部分

## Semaphore信号量(灯)
- 限流，最多的时候我允许你有多少个线程同时运行。
- acquire，得到，这是一个阻塞方法，当来一个线程时，调用acquire，信号量减1，信号量为0时，别的线程就得等着。

- 就是用来控制同时运行的线程，比如你有100个线程，但是信号量定为2，表示你有100个线程，但是同时运行的只有两个线程。

- release，线程业务处理完毕后，一定要调用该方法，将个数还回去，否则影响别的线程的运行，会导致别的线程一直处于阻塞

代码**[c_021](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_021)**部分

## Exchanger

只能是两个线程之间，exchange方法是阻塞的，一个A线程exchange了，另一个B线程没有exchange，那么A线程就等着，阻塞。

三个线程之间没有意义。

代码**[c_022](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_022)**部分

## LockSupport

LockSupport翻译过来是**锁支持**。

代码[c_023](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_023)

## 淘宝面试题

- 实现一个容器，提供两个方法，add和size；写两个线程，线程1添加10个元素至容器中，线程2实现监控元素个数，当容器内个数达到5个的时候，线程2给出提示并结束。
    - 使用volatile方式，但是这种方式并不能解决。代码：[T01_WithoutVolatile](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T01_WithoutVolatile.java)、[T02_WithVolatile](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T02_WithVolatile.java)
    - 使用wait、notify方式解决：
        - wait会释放锁，而notify不会释放锁。
        - **在调用wait()和notify()之前，必须使用synchronized语义绑定住被wait/notify对象**，否则会报错。
        - [T03_NotifyHoldingLock](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T03_NotifyHoldingLock.java)、[T04_NotifyFreeLock](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T04_NotifyFreeLock.java)
    - 使用CountDownLatch解决。代码：[T05_CountDownLatch](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T05_CountDownLatch.java)
    - 使用LockSupport解决。代码：[T06_LockSupport](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_01_interview/T06_LockSupport.java)
    
- 写一个固定容量同步容器，拥有put和get方法，以及getCount方法，能够支持2个生产者线程以及10个消费者线程的阻塞调用。
    - 使用wait和notify/notifyAll实现：[MyContainer1](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_02_interview/MyContainer1.java)
    - 使用Lock和Condition的方式，可以更精准的指定哪些线程被唤醒：[MyContainer2](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_024_02_interview/MyContainer2.java)
    - **这两种写法应该背下来**。
    
## AQS(重要)

AQS的核心就是使用CAS的操作，操作双向链表的head和tail，替代了synchronized操作。

参考这篇文章，包含AQS讲解和ReentrantLock源码的分析(jDK1.8):[从源码角度彻底理解ReentrantLock(重入锁)](https://www.cnblogs.com/takumicx/p/9402021.html)

还可以看这篇[JAVA并发编程: CAS和AQS](https://blog.csdn.net/u010862794/article/details/72892300)

代码[c_025_AQS](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_025_AQS)

### VarHandle(JDK1.9)：

代码[c_026_VarHandle](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_026_VarHandle/T01_HelloVarHandle.java)

- 普通属性也可以进行原子性操作；
- 比反射快，直接操纵二进制码。
    
    
## ThreadLocal

代码[c_027_ThreadLocal](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_027_ThreadLocal)

用途：spring的声明式事务，保证同一个connection

参考文章：[Java并发编程：深入剖析ThreadLocal](https://www.cnblogs.com/dolphin0520/p/3920407.html)

- 实际的通过ThreadLocal创建的副本是存储在每个线程自己的threadLocals中；
- 为何threadLocals的类型ThreadLocalMap的键值为ThreadLocal对象，因为每个线程中可以有多个threadLocal变量；
- 在进行get之前必须先set，否则会报空异常。如果想在get之前不需要调用set就能正常访问的话，必须重写initalValue()方法。


## 强软弱虚
