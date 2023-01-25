package cn.niu.common.config;

import cn.hutool.core.util.StrUtil;
import cn.niu.common.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 提取配置类信息
 *
 * @author Ben
 */
public abstract class CommonConfig {
    static Properties properties;

    static {
        try (InputStream in = CommonConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 获取运行端口
     *
     * @return
     */
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if (value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static String getServerIP() {
        String value = properties.getProperty("server.ip");
        if (StrUtil.isEmpty(value)) {
            return "localhost";
        } else {
            return value;
        }
    }

    /**
     * 获取序列化算法
     *
     * @return
     */
    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return Serializer.Algorithm.JDK;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
}