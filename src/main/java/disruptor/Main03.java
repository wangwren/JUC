package disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * lambda表达式写法,对比Main02
 */
public class Main03 {

    public static void main(String[] args) throws IOException, InterruptedException {

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
