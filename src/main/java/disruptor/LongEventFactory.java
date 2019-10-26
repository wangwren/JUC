package disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 如何产生这个消息，(如何产生event)
 *
 * 产生事件(消息)的工厂，生产者
 */
public class LongEventFactory implements EventFactory<LongEvent> {


    /**
     * 产生事件(消息)的时候，产生一个一个LongEvent
     * @return
     */
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
