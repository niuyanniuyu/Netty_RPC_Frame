package cn.niu.client;

import cn.niu.common.message.RpcRequestMessage;
import cn.niu.common.service.HelloService;

/**
 * 远程框架客户端主函数
 *
 * @author Ben
 */
public class RpcClient {
    public static void main(String[] args) {
        HelloService proxyService = RpcClientManager.getProxyService(HelloService.class);
        System.out.println(proxyService.sayHello("张三"));
        System.out.println(proxyService.sayHello("李四"));
    }
}
