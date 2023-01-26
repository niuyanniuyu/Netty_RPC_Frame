package cn.niu.common.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息序号id产生器
 *
 * @author Ben
 */
public abstract class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
