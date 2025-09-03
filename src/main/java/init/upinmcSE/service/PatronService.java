package init.upinmcSE.service;

import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.dao.PatronDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.repository.jdbc.BookJdbcRepository;
import init.upinmcSE.repository.jdbc.PatronJdbcRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class PatronService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());
    private static final PatronService INSTANCE = new PatronService(
            PatronJdbcRepository.getInstance(), BookJdbcRepository.getInstance());
    private static final String NOTI = "Thêm mới độc giả thất bại";

    private PatronDAO patronDAO;
    private BookDAO bookDAO;


    public PatronService(PatronDAO patronDAO, BookDAO bookDAO) {
        this.patronDAO = patronDAO;
        this.bookDAO = bookDAO;
    }

    public static PatronService getInstance() {
        return INSTANCE;
    }

    public Integer insertPatron(Patron patron) {
        if(Objects.isNull(patron)) {
            LOGGER.warning("patron is null");
            return 0;
        }
        Connection conn = null;
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Patron> patron1 = patronDAO.getByName(patron.getName(), conn);

            if(patron1.isPresent()) {
                conn.rollback();
                return 0;
            }

            int patronId = patronDAO.insertOne(patron, conn).getId();
            conn.commit();
            return patronId;
        }catch (SQLException e){
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return 0;
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

    public String borrowBook(Patron patron, Book book) {
        Connection conn = null;
        String sql = "INSERT INTO patron_book (patron_id, book_id, status) VALUES (?, ?, ?)";
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // check book exist
            Optional<Book> bookCheck = bookDAO.getByName(book.getName(), conn);
            if(bookCheck.isEmpty()) {
                conn.rollback();
                return "Book with name " + book.getName() + " not found";
            }

            // check patron exist
            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if(patronCheck.isEmpty()) {
                conn.rollback();
                return "Patron with name " + patron.getName() + " not found";
            }

            // check borrowed
            boolean checkBorrowed = patronDAO.checkBorrowBook(patronCheck.get(), bookCheck.get(), conn);
            if(!checkBorrowed) {
                conn.rollback();
                return "This book borrowed by you";
            }

            // check available count book
            if(!bookDAO.checkAvailability(bookCheck.get().getId(), conn)) {
                conn.rollback();
                return "unavailable book";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, patronCheck.get().getId());
            ps.setInt(2, bookCheck.get().getId());
            ps.setString(3, "ON");

            int borrow = ps.executeUpdate();

            if(borrow <= 0){
                conn.rollback();
                LOGGER.warning("Lỗi không tạo được bản ghi mượn book");
                return "Borrow book failed";
            }

            // update book
            book.setAvailableCount(book.getAvailableCount() - 1);
            book.setBorrowedCount(book.getBorrowedCount() + 1);
            bookDAO.updateOne(book, conn);

            conn.commit();
            return "Borrowed book successfully";
        }catch (SQLException e){
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return "Borrowed book failed";
    }

    public String returnBook(Patron patron, Book book) {
        Connection conn = null;
        String sql = "UPDATE patron_book SET status = ? WHERE patron_id = ? AND book_id = ? AND status = ?";
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // check book exist
            Optional<Book> bookCheck = bookDAO.getByName(book.getName(), conn);
            if (bookCheck.isEmpty()) {
                conn.rollback();
                return "Book with name " + book.getName() + " not found";
            }

            // check patron exist
            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if (patronCheck.isEmpty()) {
                conn.rollback();
                return "Patron with name " + patron.getName() + " not found";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "OFF");
            ps.setInt(2, patronCheck.get().getId());
            ps.setInt(3, bookCheck.get().getId());
            ps.setString(4, "ON");

            int updated = ps.executeUpdate();

            if (updated <= 0) {
                conn.rollback();
                return "Cannot find debit note for return";
            }

            // update book counts
            book.setAvailableCount(book.getAvailableCount() + 1);
            book.setBorrowedCount(book.getBorrowedCount() - 1);
            bookDAO.updateOne(book, conn);

            conn.commit();
            return "Return successfully";
        } catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return "Return failed";
    }


}
