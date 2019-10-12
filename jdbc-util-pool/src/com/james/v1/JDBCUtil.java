package com.james.v1;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author james
 * @Description
 * @Date 2019/10/8
 *
 */
public class JDBCUtil implements JDBCWithPool {

    /**
     *  读取配置、注册驱动、初始化连接池
     */
    static {
        Properties properties = new Properties();
        // getClassLoader().getResourceAsStream 拿到的是模块的 classpath 即是src目录
        InputStream is = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(properties.isEmpty()) {
            throw new RuntimeException("properties获取的配置为空，请检查");
        }

        try {
            // 注册MySQL的驱动， 获取驱动配置文件
            String diverClassName = properties.getProperty("driverClassName");
            Class.forName(diverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 读取连接数据库需要的配置文件 并初始化连接池
        ConnectionPool.initConnection(properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"));

    }

    @Override
    public  Connection getConnection() {
        return ConnectionPool.getConnection();
    }

    /**
     * todo 代理类需要处理的方法
     *
     * @param resultSet
     * @param statement
     * @param connection
     */
    @Override
    public  void close(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 兼容持久化 resultSet todo 代理类需要处理的方法
     */
    @Override
    public  void close(Statement statement, Connection connection) {
        close(null, statement, connection);
    }

    @Override
    public void closeStatement(Statement statement) {
        close(null, statement, null);
    }

    @Override
    public void closeResultSetAndStatement(ResultSet resultSet, Statement statement) {
        close(resultSet, statement, null);
    }
}
