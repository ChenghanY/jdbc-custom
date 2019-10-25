package com.james.v3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @Author james
 * @Description
 *
 * 单独抽象出一个Connection用于适配代理机制
 *
 * @Date 2019/10/10
 */
public class JDBCUtilProxy implements InvocationHandler {
    private ConnectionPool connectionPool;

    private Object target;

    public JDBCUtilProxy(Object target, ConnectionPool connectionPool) {
        this.target = target;
        this.connectionPool = connectionPool;
    }

    /**
     *
     * @return JDBCUtilProxy
     */
    public Object bind(){
        Class clazz = target.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        Class[] interfaces = clazz.getInterfaces();
        return Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    private void before() {
        System.out.println("【代理类】前置方法已执行，处理除Connection外的入参");
    }

    /**
     * @param obj 动态确定的连接池
     */
    private void after(Object obj) {
        if (obj instanceof Connection) {
            Connection connection = (Connection) obj;
            connectionPool.close(connection);
            System.out.println("【代理类】后置方法已执行， 处理Connection入参");
        }
    }

    /**
     * 只代理目标工具的close方法
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 设置 close 方法的跳转
        if (method.getName().equals("close")) {
            before(); // 前置方法
            Object[] newArgs = new Object[args.length - 1];
            if(method.getParameterCount() == 2) {
                method.invoke(target, args[0], null);
            }
            if(method.getParameterCount() == 3) {
                method.invoke(target, args[0], args[1], null);
            }
            after(args[args.length - 1]); // 后置方法处理Connection
            return null;
        } else {
            return method.invoke(target, args);
        }
    }

}
