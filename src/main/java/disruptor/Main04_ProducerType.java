package disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.*;

/**
 * 生产者线程模式，有两种，默认Producer.MULTI 多线程的方式；Producer.SINGLE，只有一个生产者
 */
public class Main04_ProducerType {

    public static void main(String[] args) throws InterruptedException {

        LongEventFactory factory = new LongEventFactory();

        int bufferSize = 1024;

        /**
         * 新填的参数：ProducerType.SINGLE 指定生产者只有一个,单线程模式
         *
         * new BlockingWaitStrategy() 等待策略
         */
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory,bufferSize
                , Executors.defaultThreadFactory()
                , ProducerType.SINGLE
                , new BlockingWaitStrategy());

        disruptor.handleEventsWith(new LongEventHandle());

        //记得要启动啊...
        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //================我们指定的生产者是单线程模式，但是创建了多个消费者，看会出现什么情况
        final int threadCount = 50;

        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        ExecutorService service = Executors.newCachedThreadPool();

        for (long i = 0; i < threadCount; i++){
            final long threadNum = 1;
            service.submit(() ->{

                System.out.printf("Thread %s ready to start!\n", threadNum );
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < 100; j++) {
                    ringBuffer.publishEvent((event, sequence) -> {
                        event.set(threadNum);
                        System.out.println("生产了 " + threadNum);
                    });
                }
            });
        }

        service.shutdown();
        TimeUnit.SECONDS.sleep(1);
        System.out.println(LongEventHandle.count);
    }
}
