package com.james.v4;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Author james
 * @Description
 * @Date 2019/10/21
 *
 */
public class JDBCUtil {
    private static String url;
    private static String username;
    private static String password;

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
        url = properties.getProperty("url");
        password = properties.getProperty("password");
        username = properties.getProperty("username");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * todo 关闭资源的总入口，所有关闭资源方法的底层实现
     *
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
     * 兼容不需要关闭resultSet的场景
     * @param statement
     * @param connection
     */
    public static void close(Statement statement, Connection connection) {
        close(null, statement, connection);
    }

}
