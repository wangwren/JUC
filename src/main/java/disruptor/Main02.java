package disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * 使用Translator
 */
public class Main02 {

    public static void main(String[] args) {
        LongEventFactory factory = new LongEventFactory();

        int bufferSize = 1024;

        //第三个参数表示的使用的是后台线程
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory,bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(new LongEventHandle());

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();


        //从这之前都与Main01的程序一样，从这之后使用的是Translator，为java8的lambda做准备

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

    }
}
