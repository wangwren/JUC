package disruptor;

/**
 * 官网示例
 *
 * 消息
 *
 */
public class LongEvent {

    /**
     * 消息里装的值
     */
    private long value;

    public void set(long value){
        this.value = value;
    }

    @Override
    public String toString() {
        return "LongEvent{" +
                "value=" + value +
                '}';
    }
}
