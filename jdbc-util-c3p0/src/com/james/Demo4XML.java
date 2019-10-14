package com.james;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author james
 * @Description XML配置c3p0连接池不需要显示读取配置
 * @see JDBCUtil#getConnection()  内部使用dataSource = new ComboPooledDataSource();
 * @Date 2019/10/14
 */
public class Demo4XML {

    @Test
    public void  connect() throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from student");
        ResultSet resultSet = ps.executeQuery();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                sb.append(resultSet.getString(i)).append(" ");
            }
            System.out.println(sb.toString());
        }
        JDBCUtil.close(connection, ps);
    }
}
