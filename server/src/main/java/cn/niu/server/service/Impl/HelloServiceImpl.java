package cn.niu.server.service.Impl;


import cn.niu.server.service.HelloService;

/**
 * HelloWorld接口实现类
 *
 * @author Ben
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
        return "hello, " + msg;
    }
}
