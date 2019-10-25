package com.james.v5;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @Author james
 * @Description Connection的包装类ConnectionProxy
 *
 * 动态代理三种代理方法
 *      1. 【代理所有方法】
 *              直接修改invoke方法, 在ConnectionProxy内增加 before after方法
 *      2. 【增强被代理类的某个方法】
 *              method.getName.equals()找到方法，控制返回值
 *      3. 【增强其他接口的方法】
 *              3.1 自己实现一个MyInterface, 在ConnectionProxy的invoke中调用实现(一定要在invoke中定义实现)
 *              3.2 ConnectionProxy还可以包装多个接口实现类，运行时传入。
 *
 *
 * @Date 2019/10/23
 */
public class ConnectionProxy implements InvocationHandler, MyInterface {

    /** 静态线程池，同一种代理只用一个线程池 */
    private static ConnectionPool connectionPool;

    private Object target;

    private Class[] aimClassArr;

    private ConnectionProxy(){}

    /**
     * 业务逻辑
     *
     * @param proxy  $ProxyN
     * @param method 被代理类的方法
     * @param args   被代理类的方法的实参
     * @return 被代理类的方法被触发后的返回值
     * <p>
     * todo 可以增强N个接口实现类， 不限于(Connection)target 还可以(HttpRequest) target
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result = null;
        if (method.getName().equals("close")) {
            System.out.println("【加强】close 方法执行了");
            connectionPool.close((Connection) proxy);
        } else if (method.getName().equals("myMethod")){ // 动态代理可以为原方法添加路由
            // todo MyInterface impl = (MyInterface) proxy; impl.myMethod会拿到预期外的结果
            myMethod();
        } else {
            //todo 未被增强的方法常规调用, 这里一定是使用 target
            result = method.invoke(target, args);
            System.out.println("【未加强】");
        }
        after();
        return result;
    }

    @Override
    public void myMethod() {
        System.out.println("【集成自定义接口实现】 myMethod");
    }

    private void before() {
        System.out.println("======== before ========");
    }

    private void after() {
        System.out.println("========  after  ========");
        System.out.println();
    }

    /**
     * 集成连接池的，代理类建造者
     */
    public static class Builder {
        ConnectionProxy proxy = new ConnectionProxy();

        public Builder(Object target) {
            proxy.target = target;
        }

        public Builder buildAimClass(Class... clazzs) {
            proxy.aimClassArr = clazzs;
            return this;
        }

        public Builder buildConnectionPool(ConnectionPool connectionPool) {
            ConnectionProxy.connectionPool = connectionPool;
            return this;
        }

        public Object build() {
            if (this.proxy.aimClassArr == null) {
                // 不指定需要代理的接口，则默认代理target的所有接口
                proxy.aimClassArr = proxy.target.getClass().getInterfaces();
            }
            if (ConnectionProxy.connectionPool == null) {
                System.out.println("警告：被代理的类未指定加入的线程池");
            }
            return Proxy.newProxyInstance(Builder.class.getClassLoader(), proxy.aimClassArr ,proxy);
        }
    }
}
