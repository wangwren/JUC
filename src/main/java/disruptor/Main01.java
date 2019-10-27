package disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * 整个处理过程：
 *  1.我们现在有一个 环，环形数组 (ringBuffer)，这个 环 里装的内容是LongEvent
 *
 *  2.如何产生这个LongEvent，是通过LongEventFactory的newInstance方法来产生
 *
 *  3.拿到这个消息如何处理，是通过LongEventHandle来进行处理，处理消息的时候会默认调用onEvent方法
 */
public class Main01 {

    public static void main(String[] args) throws Exception {

        //首先把Event的工厂初始化了
        LongEventFactory factory = new LongEventFactory();

        //环形数组的大小，应该设置为2的N次方
        int bufferSize = 1024;

        /**
         * 第一个参数：产生消息的工厂。
         * 第二个参数：指定 环 的大小。
         * 第三个参数：线程工厂，指的是要产生消费者的时候，当要调用消费者的时候是在一个特定的线程中执行的，
         *           那么这个参数表明了这个线程是如何产生的。在这个线程里调用LongEventHandle的onEvent方法
         */
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory,bufferSize, Executors.defaultThreadFactory());

        //指定拿到消息之后如何处理
        disruptor.handleEventsWith(new LongEventHandle());

        //start之后，可以想象内存里就产生一个环，每个环上LongEvent也被初始化好，等待生产者的到来，生产者只需要改里面的值就可以了
        disruptor.start();

        //获取到ringBuffer，为了去publishing
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();



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

        TimeUnit.SECONDS.sleep(3);

        System.out.println(LongEventHandle.count);

        /**
         * 其实消费者就是上面创建disruptor时，构造函数里指定的【线程工厂】中的线程去消费的
         */

    }
}
