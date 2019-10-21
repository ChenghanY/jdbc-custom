package com.james.v4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @Author james
 * @Description 代理类对象
 * @Date 2019/10/21
 */
public class ConnectionProxy implements InvocationHandler {
    /** 被代理类，数据库连接*/
    private Connection connection;

    /** 业务逻辑需要注入的参数*/
    private ConnectionPool connectionPool;

    /** 构造方法注入Class对象, 用于指定代理的方法（接口内的方法）*/
    private Class[] aimClass;

    public ConnectionProxy(Connection connection, ConnectionPool connectionPool) {
        this.connection = connection;
        this.connectionPool = connectionPool;
        aimClass = new Class[]{Connection.class};
    }

    public Object bind() {
        ClassLoader classLoader = Connection.class.getClassLoader();
        return Proxy.newProxyInstance(classLoader, aimClass, this);
    }

    /**
     *
     * @param proxy  jvm生成的代理类class对象
     * @param method  根据传入的class文件（Connection.class）反射得到所有接口方法
     * @param args  反射得到的方法参数
     * @return 返回值用于定义 proxy 的代理方法实现。
     * 原理需要持久化 @param proxy 的class文件并观察
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("close")) {
            connectionPool.close(connection);
            return null;
        }
        // 未被增强的方法常规调用
        return method.invoke(connection,args);
    }
}
