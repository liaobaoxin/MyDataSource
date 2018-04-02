package com.lbx.myDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * @Author:
 * @date 2018/4/2 9:53
 */
public class MyDataSource implements DataSource {
    //数据库连接信息
    String url = "jdbc:mysql://172.31.12.180:3306/bos";
    String user = "root";
    String password = "liaoBAOxin123...";
    //创建一个连接池,因为频繁需要增删，所以选择链表数据结构，LinkedList
    LinkedList<Connection> poll = new LinkedList<Connection>();

    public MyDataSource() {
        try {
            //注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            //初始化两条连接
            for (int i = 0; i < 2; i++) {
                //创建连接
                Connection conn = DriverManager.getConnection(url, user, password);
                poll.add(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        //并发情况下，只能一个线程进去
        synchronized (this) {
            //如果池中没有连接，则创建连接
            if (poll.isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    //创建连接
                    Connection conn = DriverManager.getConnection(url, user, password);
                    poll.add(conn);
                }
            }
            //removeFirst执行方法的返回值是connection
            final Connection conn = poll.removeFirst();
            //利用动态代理技术，增强close方法
            Connection proxyconn = (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(), conn.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("close")) {
                        System.out.println("方法名称" + method.getName());
                        System.out.println("连接给回给连接池");
                        return poll.add(conn);
                    } else {
                        return method.invoke(conn, args);
                    }
                }
            });
            return proxyconn;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
