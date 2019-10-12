package com.james.v2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author james
 * @Description 集成自定义连接池
 *
 * @Date 2019/10/8
 */
public class ConnectionPool {
    private static volatile LinkedList<Connection> pool = null;
    // 默认连接池容积为20
    private static int capacity = 20;
    // 维护一个size成员变量同步连接池内的连接数
    private static int size;

    private ConnectionPool() {}

    public static int getSize() {
        return size;
    }

    public static void setCapacity(int newCapacity) {
        if (newCapacity < 1) {
            throw new IllegalArgumentException("连接池容积不可小于1");
        }
        if(capacity != newCapacity) {
            // todo 待补充容积变化的逻辑
        }
        ConnectionPool.capacity = newCapacity;
    }

    /**
     * 初始化连接池 ， 双重检测防止重复初始化
     * @param url
     * @param username
     * @param password
     */
    public static LinkedList<Connection> initConnection(String url, String username, String password) {
        if (pool == null) {
            synchronized (ConnectionPool.class) {
                if (pool == null) {
                    // double check 单例模式
                    pool = new LinkedList<>();
                    try {
                        for (int i = 0; i < capacity; i++) {
                            Connection connection = DriverManager.getConnection(url, username, password);
                            pool.addLast(connection);
                        }
                        size = pool.size();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pool;
    }

    /**
     * 从线程池获取连接
     *     todo 线程安全问题未处理， 边界条件可能出现 null 值
     */
    public static Connection getConnection() {
        if (pool == null) {
            throw new RuntimeException("未初始化线程池");
        }
        Connection res = pool.removeFirst();
        size = pool.size();
        return res;
    }

    /**
     * 释放连接, 归还到线程池，不关闭连接
     *
     * @param connection MySQL 的 Connection
     */
    public static void releaseConnection(Connection connection) {
        if (pool == null) {
            throw new RuntimeException("未初始化线程池");
        }
        pool.addLast(connection);
        size = pool.size();
    }
}
