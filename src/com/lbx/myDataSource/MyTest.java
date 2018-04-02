package com.lbx.myDataSource;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author:
 * @date 2018/4/2 10:11
 */
public class MyTest {

    @Test
    public void fun1() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        // 1.创建自定义连接池对象
        MyDataSource dataSource = new MyDataSource();
        try {
            // 2.从池子中获取连接
            conn = dataSource.getConnection();
            String sql = "insert into t_user values(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "张三");
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("添加成功!");
            } else {
                System.out.println("添加失败!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            conn.close();
        }

    }
}
