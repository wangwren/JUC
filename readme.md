# 高并发与多线程

![juc](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/juc.jpg)

## Stargazers over time

[![Stargazers over time](https://starchart.cc/wangwren/JUC.svg)](https://starchart.cc/wangwren/JUC)

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

[彻底搞懂synchronized(从偏向锁到重量级锁)](http://wangwren.com/2019/10/%E5%BD%BB%E5%BA%95%E6%90%9E%E6%87%82synchronized-%E4%BB%8E%E5%81%8F%E5%90%91%E9%94%81%E5%88%B0%E9%87%8D%E9%87%8F%E7%BA%A7%E9%94%81/)

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
    
**可参考内容**[Java内存模型](http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html)

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

[并发的核心：CAS 是什么？Java8是如何优化 CAS 的?](http://wangwren.com/2019/10/%E5%B9%B6%E5%8F%91%E7%9A%84%E6%A0%B8%E5%BF%83%EF%BC%9ACAS-%E6%98%AF%E4%BB%80%E4%B9%88%EF%BC%9FJava8%E6%98%AF%E5%A6%82%E4%BD%95%E4%BC%98%E5%8C%96-CAS-%E7%9A%84/)

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

[重量级锁、自旋锁、轻量级锁、偏向锁、悲观、乐观锁等各种锁](http://wangwren.com/2019/10/%E9%87%8D%E9%87%8F%E7%BA%A7%E9%94%81%E3%80%81%E8%87%AA%E6%97%8B%E9%94%81%E3%80%81%E8%BD%BB%E9%87%8F%E7%BA%A7%E9%94%81%E3%80%81%E5%81%8F%E5%90%91%E9%94%81%E3%80%81%E6%82%B2%E8%A7%82%E3%80%81%E4%B9%90%E8%A7%82%E9%94%81%E7%AD%89%E5%90%84%E7%A7%8D%E9%94%81/)

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

Java中容器分两大类：Collection和Map，其中Coll8ection中又分List、Set、Queue。  

Queue是后加的，在很大程度上是为了高并发准备的(线程池中经常用)，其中阻塞队列(BlockingQueue)很重要。(这也是List和Queue的区别)。

Vector和HashTable在JDK1.0就有了，在当初设计的时候有点问题，在其内部的所有方法上都加了锁，自带锁，现在基本不用。

### Map
代码：[c_030_01_FromHashTable2CHM](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_030_01_FromHashTable2CHM)

Map的进化历程：HashTable -> HashMap -> SynchronizedHashMap -> ConcurrentHashMap

跳表参考:[跳表（SkipList）及ConcurrentSkipListMap源码解析](http://blog.csdn.net/sunxianghuang/article/details/52221913)

[T01_ConcurrentHashMap](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T01_ConcurrentHashMap.java)

### Collection

代码：[c_030_02_FromVector2Queue](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_030_02_FromVector2Queue)

进化历程：Vector -> List -> SynchronizedList -> Queue

- 同步容器类:
    - Vector、HashTable ：早期使用synchronized实现。
    - ArrayList、HashSet ：未考虑多线程安全(未实现同步)。
    - Collections.synchronizedXXX：其内部也是使用的synchronized。
    
使用早期的**同步容器**以及`Collections.snchronizedXXX`方法的不足之处，参考：
[java集合框架【3】 java1.5新特性 ConcurrentHashMap、Collections.synchronizedMap、Hashtable讨论](http://blog.csdn.net/itm_hadf/article/details/7506529)

使用新的并发容器：[jdk1.5新特性 ConcurrentHashMap](http://xuganggogo.iteye.com/blog/321630)

#### CopyOnWriteList
代码：[T02_CopyOnWriteList](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T02_CopyOnWriteList.java)
- 写时复制容器 copy on write
- 多线程环境下，写时效率低，读时效率高；适合读多写少的环境。
- CopyOnWriteArrayList源码中，写时会加锁，并建了一个新的数组，并比之前的数组+1大小，将新元素放入新的位置


![copyonwritelistadd](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/copyonwritelistadd.png)


- 读的时候不加锁，因为读的时候，新的和老的元素都一样，不需要加锁。

![get](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/copyonwirteget.png)

### Queue
- offer，对应add，加后会有一个返回值，成功返回true，不成功返回false
- add加不进去则会抛出异常。在Queue中经常使用offer。
- peek，取元素，但是并不会删除掉该元素。
- poll，取元素，并且删除掉该元素。

ConcurrentQueue都是线程安全的操作。[T03_ConcurrentQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T03_ConcurrentQueue.java)

#### BlockingQueue

LinkedBlockingQueue无界的，最大是Integer.MAX_VALUE；ArrayBlockingQueue有界的。

[T04_LinkedBlockingQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T04_LinkedBlockingQueue.java)

[T05_ArrayBlockingQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T05_ArrayBlockingQueue.java)

BlockingQueue在Queue的基础上多了`put`和`take`方法(这两个方法才体现了Blocking，offer是不会阻塞的)。
put元素时如果慢了，则线程会阻塞住；take取元素时，没有元素了会阻塞住。
BlockingQueue是天生的友好的**生产者消费者模型**

##### DelayQueue
- 是BlockingQueue的一种，也是一种阻塞的队列。区别是DelayQueue可以按等待时间排序。通过compareTo来做比较的，可以自己指定比较规则。
- 一般用来按时间进行任务调度。
- 实际上是通过PriorityQueue来实现的。

[T06_DelayQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T06_DelayQueue.java)

##### SynchronusQueue(同步Queue)
- 两个线程间的同步交换。
- **容量为0**，不是用来存放元素的，而是用来给别的线程下达任务的。
- 只能用来阻塞式的put的调用，等着别人take；如果使用add方法，则直接报错，因为容量为0，不可以往里面放元素。

[T08_SynchronousQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T08_SynchronousQueue.java)

##### TransferQueue
- 等待多个线程的取走、消费；而SynchronusQueue是一个线程对一个线程。
- 多了`transfer`方法，该方法装元素，装完等着，有人来取才会继续。
- 而使用 `put` 方法装完就不管了，装完就继续执行了，不等待。

[T09_TransferQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T09_TransferQueue.java)

#### PriorityQueue
PriorityQueue是有排序的，内部是一课堆排序的树结构。

[T07_PriorityQueue](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_031/T07_PriorityQueue.java)

### 面试题
- **Queue和List的区别**
    - Queue多了对线程友好的API(offer、peek、poll)；
    - BlockingQueue又多了take，put，这两个是阻塞的API，是生产者消费者的模型。
    
## 面试题(交替打印)
- 题：使用两个线程，线程1打印 A B C ...;线程2打印 1 2 3...最后打印出的结果要是A1B2C3这种交替打印。
- **在调用wait()和notify()之前，必须使用synchronized语义绑定住被wait/notify对象**，否则会报错。

- 提供了多种实现方式，难易程度：LockSupport cas BlockingQueue wait-notify lock-condition (仅供参考)。

代码：[c_028_interview](https://github.com/wangwren/JUC/tree/master/src/main/java/juc/c_028_interview/A1B2C3)

## 线程池

- [T01_MyExecutor](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T01_MyExecutor.java)
- [T02_ExecutorService](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T02_ExecutorService.java)
- Callable就类型与Runnable，但是Callable有返回值，一般用在线程池。[T03_Callable](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T03_Callable.java)
- Future存储执行的将来才会产生的结果。[T03_Callable](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T03_Callable.java)
- FutureTask，相当于Future + Runnable，即可以执行，又可以存结果。[T04_FutureTask](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T04_FutureTask.java)
- CompletableFuture，可以用来管理多个Future的结果(这只是其中一个)，其底层是ForkJoinPool。**这个类很强大，可以多搜一搜**。
    - [T05_CompletableFuture](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T05_CompletableFuture.java)

### 两种线程池
- **ThreadPoolExecutor**
    - 线程池忙(包括指定的最大线程数)，而且任务队列满，这时候会进行**拒绝策略**(拒绝策略可以自定义，但是JDK提供了四种)。
    - 四种拒绝策略：(实战上都不用，一般都自定义)
        - AbortPolicy：抛异常。
        - DiscardPolicy：扔掉，不抛异常。
        - DiscardOldestPolicy：扔掉排队时间最久的。
        - CallerRunsPolicy：调用者处理任务。谁提交任务，谁来执行。
        
    - 有两种执行线程池的方法execute和submit，这两个方法都可以启动线程，但是submit有返回值，可以返回一个future对象
        
- **ForkJoinPool**
    - 分解汇总的任务
    - 用很少的线程可以执行很多的任务(子任务)，ThreadPoolExecutor做不到先执行子任务。
    - CPU密集型
        
**简单使用及参数说明，这个要背下来**：[T06_HelloThreadPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T06_HelloThreadPool.java)

#### 调整线程池的大小


![threadsize](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/threadsize.png)


### Executors
- Executors，可以看成是线程池的工厂，可以用来产生各种各样的线程池。
- 其底层都是靠ThreadPoolExecutor实现的。

> Executors返回的线程池对象的弊端如下：
> 1. FixedThreadPool和SingleThreadExecutor：允许的请求队列长度为`Integer.MAX_VALUE`，可能会堆积大量的请求，从而导致OOM。
> 2. CachedThreadPool和ScheduledThreadPool：允许的创建线程数量为`Integer.MAX_VALUE`，可能会创建大量的线程，从而导致OOM。

#### SingleThreadExecutor
`Executors.newSingleThreadExecutor`只有一个线程的线程池，可以保证任务扔进去的顺序。

可以去看源码实现，都是使用的ThreadPoolExecutor类来指定的。

- 为什么要有单线程的线程池？
    - 线程池是有任务队列的。任务数量最大是Integer.MAX_VALUE
    - 有线程的完整的生命周期管理。
    

![singleThreadExecutor](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/singleThreadExecutor.png)

    
代码：[T07_SingleThreadExecutor](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T07_SingleThreadExecutor.java)
    
    
#### CachedThreadPool
- 没有核心线程
- 最大线程数为Integer.MAX_VALUE
- 线程空闲时间的为60秒
- 任务队列为SynchronousQueue，这个Queue的容量为0。

这个线程池，来任务就起一个新的线程，不会放入队列中，因为任务队列为SynchronousQueue的容量为0.

![cachedthreadpool](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/cachedthreadpool.png)

代码：[T08_CachedThreadPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T08_CachedThreadPool.java)

#### FixedThreadPool
- 固定的线程数。可以自己指定线程数。
- **确确实实可以让任务并行处理的，可以提高效率**。

![fixthreadpool](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/fixthreadpool.png)

代码：[T09_FixedThreadPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T09_FixedThreadPool.java)

#### ScheduledThreadPool
- 也可以指定核心线程数。
- 专门用来执行**定时任务的线程池**
- 它的任务队列是DelayedWorkQueue，可以指定多长时间之后运行。


![scheduledthreadpool](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/scheduledthreadpool.png)



代码：[T10_ScheduleThreadPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T10_ScheduleThreadPool.java)

##### Cached vs Fixed
阿里都不用，自己估算，进行精确定义。

### 并发(concurrent) vs 并行(parallel)
- 并发，并发是指任务**提交**。
- 并行，并行是指任务**执行**。

并行是并发的子集。

## TheadPoolExecutor源码解析
[ThreadPoolExecutor源码解析](http://wangwren.com/2019/10/ThreadPoolExecutor%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90/)

### ForkJoinPool

![51571581502_](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/51571581502_.pic.jpg)

-  **Fork/Join框架**：就是在必要的情况下，将一个大任务，进行拆分(fork)成若干个小任务(拆到不可再拆时)，再将一个个的小任务运算的结果进行join汇总。

#### Fork/Join框架与传统线程池的区别
- 采用**工作窃取**模式(work-stealing):
    - 当执行新的任务时，它可以将其拆分成更小的任务执行，并将小任务加到线程队列中，然后再从一个随机线程的队列中偷一个并把它放在自己的队列中。
- 相对于一般的线程池实现，fork/join框架的优势体现在对其中包含的任务的处理方式上。在一般线程池中，如果一个线程正在执行的任务由于某些原因无法继续运行，那么该线程会处于等待状态。
- 而在fork/join框架实现中，如果某个子问题由于等待另外一个子问题的完成而无法继续运行，**那么处理该子问题的线程会主动寻找其他尚未运行的子问题来执行**，这种方式减少了线程的等待时间，提高了性能。

代码：[T12_ForkJoinPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T12_ForkJoinPool.java)

### WorkStealing
- 每一个线程都有一个自己的队列，当自己队列中的任务执行完毕后，会去别的线程的队列上拿一个任务来执行。
- 本质上是一个ForkJoinPool。

![workstealing](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/workstealing.png)


代码：[T11_WorkStealingPool](https://github.com/wangwren/JUC/blob/master/src/main/java/juc/c_032_ThreadPool/T11_WorkStealingPool.java)

## Disruptor
[Disruptor](https://github.com/wangwren/JUC/tree/master/src/main/java/disruptor)