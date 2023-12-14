package Utils;

import java.sql.*;

public class JdbcUtils {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/xll";
    private static final String name = "root";
    private static final String password = "123456";

    static {
        try {
            Class.forName(driver);
            DriverManager.getConnection(url, name, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取数据库连接对象
     * @return 数据库连接对象
     */
    public static Connection getConnect() {
        Connection conn;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, name, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
    /**
     * 关闭数据库相关资源
     * @param conn 数据库连接对象
     * @param ps sql语句执行对象
     * @param rs 查询结果集
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (conn != null) conn.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 关闭数据库相关资源
     * @param conn 数据库连接对象
     * @param ps sql语句执行对象
     */
    public static void close(Connection conn, PreparedStatement ps) {
        close(conn, ps, null);
    }
    /**
     * 关闭数据库相关资源
     * @param conn 数据库连接对象
     * @param rs 查询结果集
     */
    public static void close(Connection conn, ResultSet rs) {
        close(conn, null, rs);
    }
}
 