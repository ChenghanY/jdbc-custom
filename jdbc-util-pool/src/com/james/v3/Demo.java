package com.james.v3;


import com.james.v2.JDBCWithPool;
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
        // 获取数据库连接池
        ConnectionPool connectionPool = new ConnectionPool();
        // 从连接池内获取数据库连接
        Connection connection = connectionPool.getConnection();

        System.out.println("成功获取连接，连接池容量为" + connectionPool.getSize());

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement("select * from score where c_id = ?");
            preparedStatement.setObject(1, "01");
            resultSet = preparedStatement.executeQuery();
            // 遍历resultSet
            if (resultSet.next()) {
                System.out.println("使用数据库连接获取 resultSet :" + resultSet.getString("s_score"));
            }
            JDBCUtil.close(preparedStatement, connection);
            System.out.println("close 方法执行后线程池容量" + connectionPool.getSize());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
