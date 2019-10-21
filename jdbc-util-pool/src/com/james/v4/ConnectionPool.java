package com.james.v4;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author james
 * @Description 集成自定义连接池
 *
 * 使用动态代理处理Connection的close方法
 * 动态代理使用方法二 : 外部新建代理类
 *
 * @see ConnectionProxy
 * @Date 2019/10/21
 */
public class ConnectionPool {
    private  LinkedList<Connection> pool = new LinkedList<>();

    /** 默认初始化连接数 */
    private int initCount = 5;

    /** 默认最大的连接个数 */
    private int maxCount = 10;

    /** @see ConnectionPool#createConnection 维护该变量 : 统计已生成完的连接 */
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

    private void createConnection() {
        Connection proxyConnection = null;
        try {
            // 使用JDBCUtil获取连接
            Connection connection = JDBCUtil.getConnection();
            // 使用动态代理，产生代理对象
            proxyConnection = (Connection) new ConnectionProxy(connection, this).bind();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createdCount++;
        pool.addLast(proxyConnection);
    }

    public Connection getConnection() {
        // 情况一: 先判断连接池是否有连接
        if (pool.size() > 0) {
            return pool.removeFirst();
        }

        // 情况二: 连接池中没有连接，然后先判断目前的连接个数是否已经超过了最大的连接个数
        if (createdCount < maxCount) {
            // 创建连接
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
