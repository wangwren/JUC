package disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 消息的消费者
 *
 * 拿到消息如何处理
 */
public class LongEventHandle implements EventHandler<LongEvent> {


    /**
     * 记录处理消息的数量，总共处理了多少条消息
     */
    public static long count = 0;

    /**
     * 处理消息的时候默认调用onEvent方法
     * @param event 表示处理的是哪个消息
     * @param sequence RingBUffer的序号，哪个位置上的消息
     * @param endOfBatch 是否为最后一个元素;整体的消息是否结束，true：整个消费者就可以退出了；false表示还有，消费者继续消费
     * @throws Exception
     */
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        count ++;
        System.out.println("[" + Thread.currentThread().getName() + "] " + event + "序号：" + sequence);
    }
}
