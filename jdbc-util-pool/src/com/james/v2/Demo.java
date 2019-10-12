package com.james.v2;


import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author james
 * @Description
 * @Date 2019/10/8
 */
public class Demo {
    @Test
    public void testConnection() {

        // 获取数据库连接
        JDBCWithPool jdbcWithPool = (JDBCWithPool) new JDBCUtilProxy(new JDBCUtil()).bind();


        Connection connection = jdbcWithPool.getConnection();
        System.out.println("成功获取连接，连接池容量为" + ConnectionPool.getSize());

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("select * from score where c_id = ?");
            preparedStatement.setObject(1, "01");
            resultSet = preparedStatement.executeQuery();
            // 遍历resultSet
            if (resultSet.next()) {
                System.out.println("使用数据库连接获取 resultSet :" + resultSet.getString("s_score"));
            }
            jdbcWithPool.close(preparedStatement, connection);
            System.out.println("close 方法执行后程池容量" + ConnectionPool.getSize());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
