package com.james.v1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface JDBCWithPool {
    Connection getConnection();

    void close(ResultSet resultSet, Statement statement, Connection connection);

    void close(Statement statement, Connection connection);

    void closeStatement(Statement statement);

    void closeResultSetAndStatement(ResultSet resultSet, Statement statement);
}
