package com.james.v3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author james
 * @Description 集成自定义连接池
 *
 * 使用动态代理处理Connection的close方法
 * 动态代理使用方法一 : 内部类封装代理类
 *
 * @Date 2019/10/21
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

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
            // todo 与JDBCUtil的唯一耦合，分离了JDBCUtil的其他职责
            Connection connection = JDBCUtil.getConnection();
            proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionPool.class.getClassLoader(),
                    new Class[]{Connection.class}, // 具体要代理的是哪个接口的实现类
                    new ConnectionHandler(connection) // 传入代理类
            );
            pool.add(proxyConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createdCount++;
    }

    /**
     * 内置被代理对象的封装类
     *
     * @see Proxy#h
     */
    class ConnectionHandler implements InvocationHandler {
        // 在内部维护一个需要被增强的对象（被代理对象）
        private Connection connection;

        private ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        /**
         * 动态代理核心方法
         * @param proxy  jvm 生成的中间代理类， 按照一定规格拼接
         * @param method proxy 中通过预定的方法反射获取的方法
         * @param args  通过参数 interfaces[] 能取到具体接口里面方法对应的入参
         * @return 不增加方法直接返回method.invoke(被代理对象, args);
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 找出需要增强的方法，进行增强
            String methodName = method.getName();
            if ("close".equalsIgnoreCase(methodName)) {
                // todo 可以强转的内在原因
                // todo proxy 通过 newInstance() 传入的 interfaces[] 拼接出的类 Proxy implements Connection
                pool.addLast((Connection) proxy);
            } else { // 其他方法正常调用
                return method.invoke(connection, args);
            }
            return null;
        }
    }

    /**
     * 取代JDBCUtil的getConnection
     * @see JDBCUtil#getConnection()
     * @return 连接池内的连接
     */
    public Connection getConnection() {
        if (pool.size() > 0) {
            return pool.removeFirst();
        }
        // 连接池超出初始化数量，每次申请连接先创建再返回
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
