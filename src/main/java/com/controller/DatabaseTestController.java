package com.controller;



import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.utils.R;

@RestController
@RequestMapping("/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-connection")
    public R testDbConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return R.ok("数据库连接成功");
        } catch (SQLException e) {
            return R.error("数据库连接失败: " + e.getMessage());
        }
    }
}