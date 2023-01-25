package cn.niu.server.handler;

import cn.niu.common.message.RpcRequestMessage;
import cn.niu.common.message.RpcResponseMessage;
import cn.niu.server.service.HelloService;
import cn.niu.server.service.factories.ServicesFactory;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * PRC远程调用消息handler
 *
 * @author Ben
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            // 获取真正的实现对象
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            // 获取要调用的方法
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());

            // 调用正常
            rpcResponseMessage.setReturnValue(invoke);

        } catch (Exception e) {
            log.error("发生异常 {}", e.toString());
            rpcResponseMessage.setReturnValue(e);
        }

        // 返回结果
        ctx.writeAndFlush(rpcResponseMessage);
    }
}
