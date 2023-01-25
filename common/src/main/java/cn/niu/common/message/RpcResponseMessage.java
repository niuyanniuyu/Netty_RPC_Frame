package cn.niu.common.message;

import lombok.Data;
import lombok.ToString;

/**
 * RPC返回消息
 *
 * @author Ben
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
