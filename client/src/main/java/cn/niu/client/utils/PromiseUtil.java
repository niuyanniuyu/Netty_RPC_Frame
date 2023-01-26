package cn.niu.client.utils;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于存放RPC响应的结果
 *
 * @author Ben
 */
public class PromiseUtil {
    //TODO 也可以使用Redis配合过期时间实现
    /**
     * 由于调用RPC和接收RPC结果的是两个线程，因此需要使用Promise完成两个线程的消息通信
     * map<sequenceId, promise>
     */
    public static final Map<Integer, Promise<Object>> PROMISES_MAP = new ConcurrentHashMap<>();
}
