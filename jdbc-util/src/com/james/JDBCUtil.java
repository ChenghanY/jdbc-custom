package com.james;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Author james
 * @Description
 *
 * 已完成：封装 jdbc 注册驱动、获取连接、关闭资源
 * 未完成 todo 集成自定义连接池
 *
 * @Date 2019/10/8
 */
public class JDBCUtil {
    private static String username;
    private static String password;
    private static String url;

    /**
     *  注册驱动
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

        // 读取连接数据库需要的配置文件
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        url = properties.getProperty("url");

    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭资源
     * @param resultSet
     * @param statement
     * @param connection
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
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
     * 兼容没有resultSet的关闭动作
     * @param statement
     * @param connection
     */
    public static void close(Statement statement, Connection connection) {
        close(null, statement, connection);
    }

}
