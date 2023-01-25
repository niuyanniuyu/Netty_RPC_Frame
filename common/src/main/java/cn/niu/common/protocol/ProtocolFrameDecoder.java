package cn.niu.common.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 自定义协议层的分包处理解码器
 *
 * @author Ben
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * 无参构造函数
     * 用于固定解析消息的参数
     */
    public ProtocolFrameDecoder() {
        this(1024, 11, 4, 1, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
