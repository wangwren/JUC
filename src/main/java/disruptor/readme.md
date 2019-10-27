# Disruptor
## 介绍
- [主页](http://lmax-exchange.github.io/disruptor/)

- [源码](https://github.com/LMAX-Exchange/disruptor)

- [GettingStarted](https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started)

- [api](http://lmax-exchange.github.io/disruptor/docs/index.html)

- [maven](https://mvnrepository.com/artifact/com.lmax/disruptor)

- [并发框架Disruptor译文](http://ifeve.com/disruptor/)

1. disruptor:翻译为分裂，瓦解
2. 一个线程中每秒处理600万订单
3. 2011年Duke奖
4. **单机**最快的MQ
5. 性能极高，无锁CAS，**单机**支持高并发

Disruptor简单理解就是内存里用于存放元素的，高效率队列。

## Disruptor的特点
- 对比ConcurrentLinkedQueue : 链表实现，链表遍历没有数组快。

- JDK中没有ConcurrentArrayQueue，因为数组大小确定，想要扩容还有创建新的数组再复制，效率很低。

- Disruptor是数组实现的，这个数组是首尾相连的，环形的。

- 无锁，高并发，使用环形Buffer，直接覆盖（不用清除）旧的数据，降低GC频率；因为每次创建不需要new，在ringBUffer初始化的时候就已经初始化好了，在产生消息的时候只需要改里面的值就好了，已经将销量考虑到极致了。

- 实现了基于事件的生产者消费者模式（观察者模式）

## RingBuffer
- 环形队列

- RingBuffer的序号，指向下一个可用的元素

- 采用数组实现，没有首尾指针

- 对比ConcurrentLinkedQueue，用数组实现的速度更快

> 假如数组长度为8，当添加到第12个元素的时候在哪个序号上呢？用12%8决定
> 当Buffer被填满的时候到底是覆盖还是等待，由Producer决定
> **长度设为2的n次幂**，利于二进制计算，例如：12%8 = 12 & (8 - 1) 
> **pos = num & (size -1) 相当于 pos = num % size**，使用位运算更快。
> 因为数组位置从0开始，所以 12 % 8 = 4，表示放在第4个位置上，即数组下标为3。

## Disruptor开发步骤
1. 定义Event - 队列中需要处理的元素

2. 定义Event工厂，用于填充队列
    1. 这里牵扯到效率问题：disruptor初始化的时候，会调用Event工厂，对ringBuffer进行内存的提前分配
    2. GC产频率会降低

3. 定义EventHandler（消费者），处理容器中的元素

## 事件发布模板
完整代码:[Main01]()

```java
//生产者

//找到环上的可用位置
long sequence = ringBuffer.next();

try {

    //得到位置之后，直接在ringBuffer中get获取上面的消息
    LongEvent event = ringBuffer.get(sequence);

    //生产者修改其中内容改成自己需要的内容
    event.set(8888L);
} finally {

    //发布，生产，将这个位置上的消息发布出来等待消费
    ringBuffer.publish(sequence);
}

/**
 * 其实消费者就是上面创建disruptor时，构造函数里指定的【线程工厂】中的线程去消费的
 */
```

## 使用EventTranslator发布事件

完整代码:[Main02]()

```java
//使用Translator会将数据转成一个event，这种写法就是替代Main01部分的try catch
EventTranslator<LongEvent> translator = new EventTranslator<LongEvent>() {

    //EventTranslator中只有这一个方法，所以可以使用lambda
    @Override
    public void translateTo(LongEvent event, long sequence) {
        //初始化event
        event.set(6666L);
    }
};

ringBuffer.publishEvent(translator);


//===============一个参数,灵活的指定值
EventTranslatorOneArg<LongEvent,Long> translatorOneArg = new EventTranslatorOneArg<LongEvent, Long>() {
    @Override
    public void translateTo(LongEvent event, long sequence, Long arg0) {
        event.set(arg0);
    }
};

ringBuffer.publishEvent(translatorOneArg,7777L);


//============两个参数
EventTranslatorTwoArg<LongEvent,Long,Long> translatorTwoArg = new EventTranslatorTwoArg<LongEvent, Long, Long>() {
    @Override
    public void translateTo(LongEvent event, long sequence, Long arg0, Long arg1) {
        event.set(arg0 + arg1);
    }
};

ringBuffer.publishEvent(translatorTwoArg,2222L,2222L);


//===========三个参数
EventTranslatorThreeArg<LongEvent,Long,Long,Long> translatorThreeArg = new EventTranslatorThreeArg<LongEvent, Long, Long, Long>() {
    @Override
    public void translateTo(LongEvent event, long sequence, Long arg0, Long arg1, Long arg2) {
        event.set(arg0 + arg1 + arg2);
    }
};

ringBuffer.publishEvent(translatorThreeArg,3333L,3333L,3333L);


//============好多个参数
EventTranslatorVararg<LongEvent> translatorVararg = new EventTranslatorVararg<LongEvent>() {
    @Override
    public void translateTo(LongEvent event, long sequence, Object... args) {
        long result = 0;
        for (Object o : args){
            long l = (Long) o;
            result += l;
        }

        event.set(result);
    }
};

ringBuffer.publishEvent(translatorVararg,1000L,1000L,1000L,1000L);
```

## 使用Lamda表达式

完整代码:[Main03]()

```java
package disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;

/**
 * lambda表达式写法,对比Main02
 */
public class Main03 {

    public static void main(String[] args) throws IOException {

        int bufferSize = 1024;

        //LongEvent::new，当你需要一个LongEvent的时候，直接调用它的new方法
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new,bufferSize, DaemonThreadFactory.INSTANCE);

        //lambda表达式写法，因为LongEventHandle里也只有一个onEvent方法直接定义了LongEventHandle，就不需要再定义LongEventHandle类了
        disruptor.handleEventsWith((event,sequence,endOfBatch) -> System.out.println("[" + Thread.currentThread().getName() + "] " + event + "序号：" + sequence));

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //直接publishEvent，也是lambda，对比Main02
        ringBuffer.publishEvent((event, sequence) -> event.set(8888L));

        //指定多个参数
        ringBuffer.publishEvent((event,sequence,arg0) -> event.set(arg0),9999L);

        System.in.read();
    }
}
```

## ProducerType生产者线程模式
> ProducerType有两种模式 `Producer.MULTI`和`Producer.SINGLE`
> 默认是MULTI，表示在多线程模式下产生sequence
> 如果确认是单线程生产者，那么可以指定SINGLE，效率会提升
> 如果是多个生产者（多线程），但模式指定为SINGLE，会出什么问题呢？线程不安全了，指定为SINGLE，当多线程访问sequence位置上的元素时就不会加锁了。

代码：[Main04_ProducerType]()

## 等待策略(8种)
- (常用）BlockingWaitStrategy：通过线程阻塞的方式，等待生产者唤醒，被唤醒后，再循环检查依赖的sequence是否已经消费。
- BusySpinWaitStrategy：线程一直自旋等待，可能比较耗cpu。
- LiteBlockingWaitStrategy：线程阻塞等待生产者唤醒，与BlockingWaitStrategy相比，区别在signalNeeded.getAndSet,如果两个线程同时访问一个访问waitfor,一个访问signalAll时，可以减少lock加锁次数。
- LiteTimeoutBlockingWaitStrategy：与LiteBlockingWaitStrategy相比，设置了阻塞时间，超过时间后抛异常。
- PhasedBackoffWaitStrategy：根据时间参数和传入的等待策略来决定使用哪种等待策略。
- TimeoutBlockingWaitStrategy：相对于BlockingWaitStrategy来说，设置了等待时间，超过后抛异常。
- （常用）YieldingWaitStrategy：尝试100次，然后Thread.yield()让出cpu。
- （常用）SleepingWaitStrategy : sleep。

## 消费者异常处理
- 默认：disruptor.setDefaultExceptionHandler()
- 覆盖：disruptor.handleExceptionFor().with()

代码：[Main07_ExceptionHandler]()


