package cn.niu.common.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有消息类型的公共父类
 *
 * @author Ben
 */
@Data
public abstract class Message implements Serializable {
    /**
     * 根据消息类型获得对应消息类
     *
     * @param messageType
     * @return
     */
    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    /**
     * 顺序Id
     */
    private int sequenceId;

    /**
     * 消息类型
     */
    private int messageType;

    /**
     * 抽象方法，由真正的子类实现，返回具体的消息类型
     *
     * @return
     */
    public abstract int getMessageType();

    /**
     * 注册所有消息类型的版本号
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();


    static {
        // ...
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}
