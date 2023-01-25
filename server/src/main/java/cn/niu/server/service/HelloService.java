package cn.niu.server.service;


/**
 * HelloWorld接口
 *
 * @author Ben
 */
public interface HelloService {
    /**
     * 远程调用
     *
     * @param msg
     * @return
     */
    String sayHello(String msg);

}