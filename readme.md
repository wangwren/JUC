# 高并发与多线程

## 线程
- 线程的创建和启动
- 线程的sleep、yield、join
- 线程的状态

代码在 [c_000](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_000) 部分。

## synchronized关键字(**悲观锁**)

- synchronized(Object)
    - 不能用String常量、Integer、Long。
    - 锁住的是对象
    - 代码 [c_014](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_014) 部分。

- 线程同步
    - synchronized锁的是对象，不是代码。
    - **锁定方法和非锁定方法可以同步进行**
    
- synchronized优化：代码 [c_013](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_013) 部分

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

synchronized代码在[c_001](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_001) 至 [c_011](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_011) 部分。

## volatile关键字
- 保证线程间可见
    - MESI
    - 缓存一致性协议
    
- 禁止指令重排
    - DCL单例
    - Double Check Lock
    
可参考内容[Java内存模型](http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html)

volatile代码在 [c_012](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_012) 部分  
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

代码在[c_015](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_015)部分。

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

代码在[c_016](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_016)部分

## CountDownLatch 门闩

- 应用场景，可以等待其他线程都执行完毕后再继续执行别的操作。使用join方法也可以实现，但是join方法不太好控制。
- 真是场景中，我使用过CountDownLatch。开启了十个线程去查询数据，等十个线程都操作完毕后，进行打包下载。

**countDown也不是说只能在一个线程里countDown一下，也可以在一个线程里countDown N多下**，只要到0了，就继续执行剩下代码。

代码[c_017](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_017)部分

## CyclicBarrier(栅栏)

代码[c_018](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_018)部分

## Phaser(阶段)

代码[c_019](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_019)部分

## ReadWriteLock 读写锁

- **共享锁**，读锁就是共享锁，在读的时候大大提高效率。即只要是读操作，就不会阻塞等待锁释放，大家可以一起读。

- **排他锁，互斥锁**，写锁就是排他锁

代码[c_020](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_020)部分

## Semaphore信号量(灯)
- 限流，最多的时候我允许你有多少个线程同时运行。
- acquire，得到，这是一个阻塞方法，当来一个线程时，调用acquire，信号量减1，信号量为0时，别的线程就得等着。

- 就是用来控制同时运行的线程，比如你有100个线程，但是信号量定为2，表示你有100个线程，但是同时运行的只有两个线程。

- release，线程业务处理完毕后，一定要调用该方法，将个数还回去，否则影响别的线程的运行，会导致别的线程一直处于阻塞

代码[c_021](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_021)部分

## Exchanger

只能是两个线程之间，exchange方法是阻塞的，一个A线程exchange了，另一个B线程没有exchange，那么A线程就等着，阻塞。

三个线程之间没有意义。

代码[c_022](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_022)部分

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

代码:[c_029_RefType](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_029_RefType)

### 强引用
我们平常典型编码 `Object obj = new Object();`中的obj就是强引用。通过关键字new创建的对象所关联的引用就是强引用。当JVM内存空间不足，JVM宁愿抛出OOM运行时错误，使程序异常终止，也不会随意回收具有强引用的“存活”对象来解决内存不足的问题。对于一个普通对象，如果没有其他引用关系，只要超过了引用的作用域或者显示地将相应(强)引用赋值为null，就是可以被垃圾收集了，具体回收时机要看垃圾收集策略。

### 软引用(SoftReference)
软引用通过SoftReference类实现。软引用的生命周期比强引用短一些。只有当 JVM 认为内存不足时，才会去试图回收软引用指向的对象：即JVM 会确保在抛出 OutOfMemoryError 之前，清理软引用指向的对象。软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收器回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中。后续，我们可以调用ReferenceQueue的poll()方法来检查是否有它所关心的对象被回收。如果队列为空，将返回一个null,否则该方法返回队列中前面的一个Reference对象。

应用场景：软引用通常用来实现内存敏感的缓存。如果还有空闲内存，就可以暂时保留缓存，当内存不足时清理掉，这样就保证了使用缓存的同时，不会耗尽内存。

### 弱引用(WeakReference)
弱引用通过WeakReference类实现。弱引用的生命周期比软引用短。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。由于垃圾回收器是一个优先级很低的线程，因此不一定会很快回收弱引用的对象。弱引用可以和一个引用队列（ReferenceQueue）联合使用，如果弱引用所引用的对象被垃圾回收，Java虚拟机就会把这个弱引用加入到与之关联的引用队列中。

对应的ThreadLocal中的ThreadLocalMap的Entry继承的就是弱引用。

![WeakReference](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2019-10-11_23-38-09.png)

加了个图，但是在GitHub上显示不全不知道为啥，可以直接访问链接查看：[弱引用ThreadLocal中的Entry](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2019-10-11_23-38-09.png)

还有WeakHashMap，可以参考这篇文章或看源码：[WeakHashMap的详细理解](https://blog.csdn.net/qiuhao9527/article/details/80775524)

### 虚引用(PhantomReference)
虚引用也叫幻象引用，通过PhantomReference类来实现。无法通过虚引用访问对象的任何属性或函数。幻象引用仅仅是提供了一种确保对象被 finalize 以后，做某些事情的机制。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。虚引用必须和引用队列 （ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中。
```java
ReferenceQueue queue = new ReferenceQueue ();
PhantomReference pr = new PhantomReference (object, queue);
```
程序可以通过判断引用队列中是否已经加入了虚引用，来了解被引用的对象是否将要被垃圾回收。如果程序发现某个虚引用已经被加入到引用队列，那么就可以在所引用的对象的内存被回收之前采取一些程序行动。

应用场景：可用来跟踪对象被垃圾回收器回收的活动，当一个虚引用关联的对象被垃圾收集器回收之前会收到一条系统通知。

## 容器(这里介绍容器为线程池做准备)

![container](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/31571235049_.pic.jpg)

Java中容器分两大类：Collection和Map，其中Collection中又分List、Set、Queue。  

Queue是后加的，在很大程度上是为了高并发准备的(线程池中经常用)，其中阻塞队列(BlockingQueue)很重要。(这也是List和Queue的区别)。

Vector和HashTable在JDK1.0就有了，在当初设计的时候有点问题，在其内部的所有方法上都加了锁，自带锁，现在基本不用。

### Map
代码：[c_030_FromHashTable2CHM]()

Map的进化历程：HashTable -> HashMap -> SynchronizedHashMap -> ConcurrentHashMap