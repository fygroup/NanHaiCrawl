package com.ab.crawl.util;


import java.sql.*;

/** 
* @ClassName: DBUtil 
* @Description: 数据库连接
* @author gaow
* @date 2018年10月30日 下午4:33:15 
*  
*/
public class DBUtil {
	
    private static final String driverClass = PropertiesConfig.INSTANCE.getProPerties("driverClass");
    private static final String jdbcUrl = PropertiesConfig.INSTANCE.getProPerties("jdbcUrl");
    private static final String user = PropertiesConfig.INSTANCE.getProPerties("user");
    private static final String password = PropertiesConfig.INSTANCE.getProPerties("password");

    /** 
    * @Title: getConn 
    * @Description: 业务库
    * @param @return    设定文件 
    * @return Connection    返回类型 
    * @throws 
    */
    public static Connection getConn() {
        // 1.注册驱动
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 2.创建Connection(数据库连接对象)
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, user, password);
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
         * Connection是Statement的工厂，一个Connection可以生产多个Statement。
         * Statement是ResultSet的工厂，一个Statement却只能对应一个ResultSet（它们是一一对应的关系）。
         * 所以在一段程序里要用多个ResultSet的时候，必须再Connection中获得多个Statement，然后一个Statement对应一个ResultSet。
         */
        return null;
    }

    /**
     * 关闭连接(数据库连接对象)
     *
     * @param conn
     */
    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭编译的 SQL 语句的对象
     *
     * @param stmt
     */
    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭结果集
     *
     * @param rs
     */
    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
}

    /**
     * 提交事务
     *
     * @param conn
     */
    public static void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回滚事务
     *
     * @param conn
     */
    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    public static void main(String[] args) {
        conn = DBUtil.getConn();
        ResultSet i =null;
        String sql = "select * from TYPHOONINFO;";
        try {
            ps = conn.prepareStatement(sql);

            i = ps.executeQuery();
            conn.commit();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn);
        }
        System.out.println(i);
    }
}
