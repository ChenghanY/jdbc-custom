package com.james.v5;


import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author james
 * @Description
 * @Date 2019/10/21
 */
public class Demo {
    @Test
    public void testConnection() throws SQLException {
        // 获取一个线程池
        ConnectionPool connectionPool = new ConnectionPool();
        // 从连接池里面获取连接
        Connection connection = connectionPool.getConnection();

        System.out.println("【main】成功获取连接，连接池内的连接数为" + connectionPool.getSize());

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement("select * from score where c_id = ?");
            preparedStatement.setObject(1, "01");
            resultSet = preparedStatement.executeQuery();
            // 遍历resultSet
            if (resultSet.next()) {
                // System.out.println("【main】使用数据库连接获取 resultSet :" + resultSet.getString("s_score"));
            }
            JDBCUtil.close(preparedStatement, connection);
            System.out.println("【main】 close 方法执行后连接池内的连接数为" + connectionPool.getSize());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
