package cn.niu.client;

import cn.niu.client.handler.RpcResponseMessageHandler;
import cn.niu.client.utils.PromiseUtil;
import cn.niu.common.message.RpcRequestMessage;
import cn.niu.common.protocol.MessageCodecSharable;
import cn.niu.common.protocol.ProtocolFrameDecoder;
import cn.niu.common.protocol.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * RPC客户端
 *
 * @author Ben
 */
@Slf4j
public class RpcClientManager {
    private static Channel channel = null;
    private static volatile Object lock = new Object();


    //创建代理类
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};

        Object obj = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            //将方法调用转换成消息对象
            RpcRequestMessage message = new RpcRequestMessage(
                    SequenceIdGenerator.nextId(),
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            //将消息对象发送出去
            getChannel().writeAndFlush(message);

            //创建一个空Promise准备存放结果，使用当前channel的eventLoop线程接收结果
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            PromiseUtil.PROMISES_MAP.put(message.getSequenceId(), promise);


            //等待结果,超时时间1s
            promise.await(2 * 1000);

            if (promise.isSuccess()) {
                return promise.getNow();
            }
            System.out.println(promise.cause());
            return promise.cause().toString();
        }));

        return (T) obj;
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    /**
     * 初始channel
     */
    private static void initChannel() {
        log.info("client start..");

        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器
        RpcResponseMessageHandler RPC_RESPONSE_MESSAGE_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                //ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_RESPONSE_MESSAGE_HANDLER);
            }
        });

        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();

            log.info("client started");
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
                log.info("client closed..");
            });
        } catch (Exception e) {
            log.error("客户端发生异常，{}", e.toString());
        }
    }
}