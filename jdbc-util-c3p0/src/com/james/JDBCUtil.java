package com.james;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author james
 * @Description
 * @Date 2019/9/22
 */
public class JDBCUtil {
    private static ComboPooledDataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource();
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
