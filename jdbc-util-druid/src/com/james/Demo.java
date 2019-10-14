package com.james;

import org.junit.Test;

import java.sql.Connection;

/**
 * @Author james
 * @Description
 * @see JDBCUtil#getConnection()
 *
 *  dataSource = DruidDataSourceFactory.createDataSource(p);
 * @Date 2019/10/14
 */
public class Demo {
    @Test
    public void connect() {
        Connection connection = JDBCUtil.getConnection();
        System.out.println(connection);
    }
}
