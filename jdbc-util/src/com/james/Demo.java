package com.james;

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
        Connection connection = JDBCUtil.getConnection();
        System.out.println(connection);
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement("select * from score where c_id = ?");
            preparedStatement.setObject(1, "01");
            resultSet = preparedStatement.executeQuery();
            // 遍历resultSet
            if (resultSet.next()){
                System.out.println(resultSet.getString("s_score"));
            }

            // 关流的动作封装到JDBCUtil里面
            JDBCUtil.close(resultSet, preparedStatement, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
