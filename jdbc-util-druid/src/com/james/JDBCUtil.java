package com.james;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author james
 * @Description
 * @Date 2019/9/22
 */
public class JDBCUtil {
    private static DataSource dataSource;

    static {
        Properties p = new Properties();
        InputStream is = JDBCUtil.class.getClassLoader().getResourceAsStream("druid.properties");

        // todo druid 需要加载一个properties对象
        try {
            p.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return connection;
    }

    public static void close(ResultSet resultSet, Connection connection, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
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

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection connection, Statement statement) {
        close(null, connection, statement);
    }

}
