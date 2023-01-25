package cn.niu.common.protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 自定义序列化算法接口
 *
 * @author Ben
 */
public interface Serializer {
    /**
     * 序列化方法，对象 -> byte[]数组
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化方法，byte[]数组 -> 对象
     *
     * @param clazz 目标类型
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deSerialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化算法枚举
     */
    @Slf4j
    enum Algorithm implements Serializer {
        //JDK自带的序列化方式
        JDK {
            @Override
            public <T> byte[] serialize(T object) {
                ByteArrayOutputStream bos = null;
                ObjectOutputStream oos = null;
                byte[] bytes = null;
                try {
                    bos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    bytes = bos.toByteArray();
                } catch (Exception e) {
                    log.error("序列化消息失败", e);
                } finally {
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e) {
                            log.error("ObjectOutputStream关闭失败", e);
                        }
                    }
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            log.error("ByteArrayOutputStream关闭失败", e);
                        }
                    }
                }
                return bytes;
            }

            @Override
            public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
                ObjectInputStream ois = null;
                T t = null;
                try {
                    ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    t = (T) ois.readObject();
                } catch (ClassNotFoundException e) {
                    log.error("反序列化消息失败", e);
                } catch (IOException e) {
                    log.error("获取ObjectInputStream失败", e);
                } finally {
                    if (ois != null) {
                        try {
                            ois.close();
                        } catch (IOException e) {
                            log.error("ObjectInputStream关闭失败", e);
                        }
                    }
                }
                return t;
            }
        },

        //JSON方式
        JSON {
            @Override
            public <T> byte[] serialize(T object) {
                String message = com.alibaba.fastjson2.JSON.toJSONString(object);
                return message.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
                String message = new String(bytes, StandardCharsets.UTF_8);
                return com.alibaba.fastjson2.JSON.parseObject(message, clazz);
            }
        },

        //ProtoBuf方式
        PROTOBUF {
            @Override
            public <T> byte[] serialize(T object) {
                return null;
            }

            @Override
            public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
                return null;
            }
        },

        //hessian方式
        HESSIAN {
            @Override
            public <T> byte[] serialize(T object) {
                return null;
            }

            @Override
            public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
                return null;
            }
        }
    }
}
