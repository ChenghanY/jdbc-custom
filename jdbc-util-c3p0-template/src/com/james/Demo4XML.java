package com.james;

import com.james.domain.User;
import org.junit.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * @Author james
 * @Description XML配置c3p0连接池不需要显示读取配置
 * @see JDBCUtil#getConnection()  内部使用dataSource = new ComboPooledDataSource();
 * @Date 2019/10/14
 */
public class Demo4XML {

    // 从jdbcUtil内获取连接
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JDBCUtil.getDataSource());

    @Test
    public void  connect() throws SQLException {
        Connection connection = JDBCUtil.getConnection();
        String sql = "SELECT * FROM USER where id = ?";

        //todo jdbcTemplate.queryForList报错，未解决，暂时使用queryForObject
        User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), 1);
        System.out.println(user);
    }
}
