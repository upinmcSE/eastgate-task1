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
import java.util.Optional;

public class PatronService {
    private static final PatronService INSTANCE = new PatronService();
    private static final String NOTI = "Thêm mới độc giả thất bại";

    private final PatronDAO patronDAO = PatronDAO.getInstance();
    private final BookDAO bookDAO = BookDAO.getInstance();

    private PatronService() {}

    public static PatronService getInstance() {
        return INSTANCE;
    }

    public String addPatron(Patron patron) {
        String result = NOTI;
        Connection conn = null;
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Patron> patron1 = patronDAO.getByName(patron.getName(), conn);

            if(patron1.isPresent()) {
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

    public Optional<Patron> getPatronByName(String name) {
        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            Optional<Patron> patron = patronDAO.getByName(name, conn);
            if(patron.isPresent()) {
                return patron;
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return Optional.empty();
    }

    public void getAllPatrons() {
        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            List<Patron> patrons = patronDAO.getAll(conn);
            if (patrons.isEmpty()) {
                System.out.println("Không tồn tại độc giả nào");
            } else {
                patrons.forEach(System.out::println);
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void borrowBook(Patron patron, Book book) {
        Connection conn = null;
        String sql = "INSERT INTO patron_book (patron_id, book_id, status) VALUES (?, ?, ?)";
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // check book exist
            Optional<Book> bookCheck = bookDAO.getByName(book.getName(), conn);
            if(bookCheck.isEmpty()) {
                conn.rollback();
                System.out.println("Book tên " + book.getName() + " không tồn tại");
                return;
            }

            // check patron exist
            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if(patronCheck.isEmpty()) {
                conn.rollback();
                System.out.println("Patron tên " + patron.getName() + " không tồn tại");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, patronCheck.get().getId());
            ps.setInt(2, bookCheck.get().getId());
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
