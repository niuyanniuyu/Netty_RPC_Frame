package cn.niu.common.protocol;

import cn.niu.common.config.CommonConfig;
import cn.niu.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 */
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    /**
     * 对消息按固定格式(魔数、版本号、序列化方式、消息指令类型、请求序号、长度、padding、内容)编码
     *
     * @param ctx
     * @param msg
     * @param outList
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 4个字节的魔数
        out.writeBytes(new byte[]{'b', 'a', 'b', 'y'});
        // 1个字节的版本号
        out.writeByte(1);
        // 1个字节的序列化方式，根据枚举的顺序决定数值
        out.writeByte(CommonConfig.getSerializerAlgorithm().ordinal());
        // 1个字节的指令类型，例如登录、注册、私聊、群发等
        out.writeByte(msg.getMessageType());
        // 4个字节的请求序号，预留异步处理消息的能力
        out.writeInt(msg.getSequenceId());

        //获取对象输出字节流
        byte[] bytes = CommonConfig.getSerializerAlgorithm().serialize(msg);

        // 4个字节的长度
        out.writeInt(bytes.length);
        // 由于上述共15个字节，添加 padding 填充
        out.writeByte(0xff);

        // 内容
        out.writeBytes(bytes);

        //写入结果
        outList.add(out);


    }

    /**
     * 对消息按固定格式(魔数、版本号、序列化方式、消息指令类型、请求序号、长度、padding、内容)解码
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        int length = in.readInt();
        byte padding = in.readByte();

        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        //找到反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerType];
        //确定具体消息类型，因为直接传入Message.class会丢失部分属性
        Class<?> messageClass = Message.getMessageClass(messageType);
        Message message = (Message) algorithm.deSerialize(messageClass, bytes);

        if (message != null) {
            log.info("消息头信息：{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
            log.info("消息正文：{}", message);

            //存放到Netty的结果集中，给下一个handler使用
            out.add(message);
        }

    }
}
