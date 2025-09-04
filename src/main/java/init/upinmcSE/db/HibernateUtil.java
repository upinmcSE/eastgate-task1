package init.upinmcSE.db;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Loi khong the tao SessionFactory");
            e.printStackTrace();
            return null;
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }

    public static void printHibernateException(Exception e) {
        if (e instanceof JDBCException) {
            JDBCException jdbcEx = (JDBCException) e;
            System.err.println("Message: " + jdbcEx.getMessage());
            System.err.println("SQL: " + jdbcEx.getSQL());
            System.err.println("SQLState: " + jdbcEx.getSQLException().getSQLState());
            System.err.println("Error Code: " + jdbcEx.getSQLException().getErrorCode());

            Throwable t = jdbcEx.getCause();
            while (t != null) {
                System.err.println("Cause: " + t);
                t = t.getCause();
            }
        } else if (e instanceof HibernateException) {
            e.printStackTrace(System.err);
        } else {
            e.printStackTrace(System.err);
        }
    }

}
