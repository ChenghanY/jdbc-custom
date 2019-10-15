package com.james;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author james
 * @Description
 * @Date 2019/10/14
 */
public class Demo4Normal {

    @Test
    public void connect() throws PropertyVetoException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // 必要配置
        // Driver路径
        dataSource.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        // url
        dataSource.setJdbcUrl("jdbc:mysql:///practice");
        // user
        dataSource.setUser("root");
        // password
        dataSource.setPassword("root");

        // 可选配置
        dataSource.setAcquireIncrement(2);
        dataSource.setInitialPoolSize(4);
        dataSource.setMaxPoolSize(10);
        dataSource.setMaxIdleTime(5);
        dataSource.setMinPoolSize(2);

        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }
}
