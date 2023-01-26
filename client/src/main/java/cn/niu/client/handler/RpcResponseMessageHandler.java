package cn.niu.client.handler;

import cn.niu.client.utils.PromiseUtil;
import cn.niu.common.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理RPC远程调用回复消息handler
 *
 * @author Ben
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage msg) {
        //将结果放入与消息序列id对应的Promise中，为防止内存泄露使用remove直接移除
        Promise<Object> promise = PromiseUtil.PROMISES_MAP.remove(msg.getSequenceId());

        //保护逻辑，判断是否能获取到对应Promise，排除序列id错误等原因
        if (promise == null) {
            log.error("无法找到匹配promise,sequenceId={}", msg.getSequenceId());
            return;
        }

        //判断RPC调用是否有结果
        if (msg.getExceptionValue() != null) {
            promise.setFailure(msg.getExceptionValue());
        } else {
            promise.setSuccess(msg.getReturnValue());
        }
    }
}
