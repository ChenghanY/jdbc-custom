package com.james.v5;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author james
 * @Description
 * @Date 2019/10/21
 * @see ConnectionProxy 被代理类的包装类
 * @see JDBCUtil 用于获取原生连接
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    /**
     * 默认初始化连接数
     */
    private int initCount = 5;

    /**
     * 默认最大的连接个数
     */
    private int maxCount = 10;

    /**
     * @see ConnectionPool#createConnection 维护该变量 : 统计已生成完的连接
     */
    private int createdCount = 0;

    public ConnectionPool() {
        for (int i = 0; i < initCount; i++) {
            createConnection();
        }
    }

    public ConnectionPool(int initCount, int maxCount) {
        this.initCount = initCount;
        this.maxCount = maxCount;
        for (int i = 0; i < initCount; i++) {
            createConnection();
        }
    }

    public int getSize() {
        return this.pool.size();
    }

    /**
     * 使用动态代理的业务场景
     * <p>
     * 一个 proxy 需要给指定为一到多种的实现类
     * 用多态的特性，抽象的proxy 可以被动态得绑定为接口的实现类型
     */
    private void createConnection() {
        Connection connection;  // 被代理对象
        Connection proxyConnection = null; // 动态生成的拥有Connection属性的代理者
        MyInterface impl; // 动态生成的拥有MyInterface属性的代理者
        Object proxy;
        try {
            // todo 与JDBCUtil的唯一耦合，分离了JDBCUtil的其他职责
            connection = JDBCUtil.getConnection();
            proxy = new ConnectionProxy.Builder(connection)
                    .buildConnectionPool(this)
                    // todo 记得将所有需要代理的接口传入
                    .buildAimClass(Connection.class, MyInterface.class)
                    .build();

            proxyConnection = (Connection) proxy;
            impl = (MyInterface) proxy;
            impl.myMethod();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createdCount++;
        pool.addLast(proxyConnection);
    }

    public Connection getConnection() {
        if (pool.size() > 0) {
            return pool.removeFirst();
        }
        if (createdCount < maxCount) {
            createConnection();
            return pool.removeFirst();
        } else {
            throw new RuntimeException("已经达到了最大的连接个数，请稍后");
        }
    }

    public void close(Connection connection) {
        pool.addLast(connection);
    }
}
