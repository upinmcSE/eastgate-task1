package init.upinmcSE.service;

import com.mysql.cj.jdbc.JdbcConnection;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.dao.PatronDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PatronService {
    private final String NOTI = "Thêm mới độc giả thất bại";
    private PatronDAO patronDAO;
    private BookDAO bookDAO;

    public static PatronService getInstance() { return new PatronService(); }

    public String addPatron(Patron patron) {
        String result = NOTI;
        PatronDAO patronDAO = PatronDAO.getInstance();
        Connection conn = null;
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Patron patron1 = patronDAO.getByName(patron.getName(), conn);

            if(!Objects.isNull(patron1)) {
                conn.rollback();
                result = "Độc giả đã tồn tại";
                return result;
            }

            int patronId = patronDAO.insertOne(patron, conn);
            conn.commit();
            return "Thêm mới độc giả với id " + patronId;
        }catch (SQLException e){
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return result;
    }

    public void getPatronByName(String name) {
        PatronDAO patronDAO = PatronDAO.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            Patron patron = patronDAO.getByName(name, conn);

            if(Objects.isNull(patron)) {
                System.out.println("Không tồn tại độc giả với " + name);
            }else{
                System.out.println(patron);
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void getAllPatrons() {
        PatronDAO patronDAO = PatronDAO.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            List<Patron> patrons = patronDAO.getAll(conn);
            if(Objects.isNull(patrons)) {
                System.out.println("Không tồn tại độc giả nào");
            }else{
                for (Patron patron : patrons) {
                    System.out.println(patron);
                }
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void borrowBook(Patron patron, Book book) {
        bookDAO = BookDAO.getInstance();
        patronDAO = PatronDAO.getInstance();
        Connection conn = null;

        String sql = "INSERT INTO patron_book (patron_id, book_id, status) VALUES (?, ?, ?)";

        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // check book exist
            Book bookCheck = bookDAO.getByName(book.getName(), conn);
            if(Objects.isNull(bookCheck)) {
                conn.rollback();
                System.out.println("Book tên " + book.getName() + " không tồn tại");
                return;
            }

            // check patron exist
            Patron patronCheck = patronDAO.getByName(patron.getName(), conn);
            if(Objects.isNull(patronCheck)) {
                conn.rollback();
                System.out.println("Patron tên " + patron.getName() + " không tồn tại");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, patronCheck.getId());
            ps.setInt(2, bookCheck.getId());
            ps.setString(3, "ON");

            ps.executeUpdate();
            conn.commit();
            System.out.println("Mượn sách thành công");
        }catch (SQLException e){
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
    }

}
