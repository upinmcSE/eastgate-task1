package init.upinmcSE.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {
    private Connection conn;
    private final String url = "jdbc:mysql://localhost:3306/eastgate_software?allowPublicKeyRetrieval=true";
    private final String username = "root";
    private final String password = "root";

    public static JDBCUtil getInstance() { return new JDBCUtil(); }

    public Connection getConnection() {
        try {
            return this.conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    public void printSQLException(Exception ex) {
        if(ex instanceof SQLException){
            ex.printStackTrace(System.err);
            System.err.println("SQLState: " + ((SQLException) ex).getSQLState());
            System.err.println("Error Code: " + ((SQLException) ex).getErrorCode());
            System.err.println("Message: " + ex.getMessage());
            Throwable t = ex.getCause();
            while(t != null){
                System.out.println("Cause: " + t);
                t = t.getCause();
            }
        }else{
            ex.printStackTrace(System.err);
        }
    }

    public void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                JDBCUtil.getInstance().printSQLException(rollbackEx);
            }
        }
    }

    public void closeConnection(Connection conn) {
        try {
            if (conn != null && conn.isClosed()) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    JDBCUtil.getInstance().printSQLException(e);
                }
            }
        } catch (Exception e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }
}
