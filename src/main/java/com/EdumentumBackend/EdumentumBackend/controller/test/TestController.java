package com.EdumentumBackend.EdumentumBackend.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DeleteMapping("/drop-table/{tableName}")
    public String dropTable(@PathVariable String tableName) {
        try {
            String sql = "DROP TABLE IF EXISTS " + tableName;
            jdbcTemplate.execute(sql);
            return "✅ Dropped table: " + tableName;
        } catch (Exception e) {
            return "❌ Error dropping table: " + e.getMessage();
        }
    }
}
