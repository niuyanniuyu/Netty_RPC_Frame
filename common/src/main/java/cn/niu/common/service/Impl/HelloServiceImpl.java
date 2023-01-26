package cn.niu.common.service.Impl;


import cn.niu.common.service.HelloService;

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
