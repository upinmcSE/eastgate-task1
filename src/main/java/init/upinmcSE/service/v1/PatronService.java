package init.upinmcSE.service.v1;

import init.upinmcSE.repository.custom.BookRepository;
import init.upinmcSE.repository.custom.PatronRepository;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class PatronService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());
    private static final String NOTI = "Thêm mới độc giả thất bại";
    private PatronRepository patronDAO;
    private BookRepository bookDAO;

    public PatronService() {}

    public PatronService(PatronRepository patronDAO, BookRepository bookDAO) {
        this.patronDAO = patronDAO;
        this.bookDAO = bookDAO;
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
                return 0;
            }

            Patron insertPatron = (Patron) patronDAO.insertOne(patron, conn);
            conn.commit();
            return insertPatron.getId();
        }catch (Exception e){
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
        }catch (Exception e){
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
        }catch (Exception e){
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public String borrowBook(Patron patron, Book book) {
        if(Objects.isNull(patron) || Objects.isNull(book)) {
            LOGGER.warning("Inputs is null");
            return "Inputs is null";
        }
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
        }catch (Exception e){
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
                return "Book with name " + book.getName() + " not found";
            }

            // check patron exist
            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if (patronCheck.isEmpty()) {
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

            // update book
            book.setAvailableCount(book.getAvailableCount() + 1);
            book.setBorrowedCount(book.getBorrowedCount() - 1);
            bookDAO.updateOne(book, conn);

            conn.commit();
            return "Return successfully";
        } catch (Exception e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return "Return failed";
    }

    public String borrowBookOptimistic(Patron patron, Book book) {
        if(Objects.isNull(patron) || Objects.isNull(book)) {
            LOGGER.warning("Inputs is null");
            return "Inputs is null";
        }

        Connection conn = null;
        String atomicUpdateSql =
        """
        UPDATE books 
        SET available_count = available_count - 1, 
            borrowed_count = borrowed_count + 1, 
            version = version + 1 
        WHERE id = ?    
        AND version = ? 
        AND available_count > 0
        """;

        String insertBorrowSql = "INSERT INTO patron_book (patron_id, book_id, status) VALUES (?, ?, ?)";

        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if(patronCheck.isEmpty()) {
                return "Patron not found";
            }

            Optional<Book> bookCheck = bookDAO.getByName(book.getName(), conn);
            if(bookCheck.isEmpty()) {
                return "Book not found";
            }

            Book currentBook = bookCheck.get();

            boolean alreadyBorrowed = patronDAO.checkBorrowBook(patronCheck.get(), currentBook, conn);
            if(!alreadyBorrowed) {
                return "Book already borrowed by you";
            }

            PreparedStatement updatePs = conn.prepareStatement(atomicUpdateSql);
            updatePs.setInt(1, currentBook.getId());
            updatePs.setLong(2, currentBook.getVersion());

            int updateResult = updatePs.executeUpdate();

            if(updateResult == 0) {
                conn.rollback();
                return "Book not found during recheck";
            }

            PreparedStatement insertPs = conn.prepareStatement(insertBorrowSql);
            insertPs.setInt(1, patronCheck.get().getId());
            insertPs.setInt(2, currentBook.getId());
            insertPs.setString(3, "ON");

            int borrowResult = insertPs.executeUpdate();
            if(borrowResult <= 0) {
                conn.rollback();
                return "Failed to create borrow record";
            }

            conn.commit();
            return "Borrowed book successfully";
        } catch (Exception e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
            return "Database error occurred";
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
    }


    public String borrowBookPessimistic(Patron patron, Book book) {
        if(Objects.isNull(patron) || Objects.isNull(book)) {
            LOGGER.warning("Inputs is null");
            return "Inputs is null";
        }

        Connection conn = null;

        String selectForUpdateSql = """
                                    SELECT id, name, available_count, borrowed_count, version
                                    FROM books 
                                    WHERE name = ? 
                                    FOR UPDATE NOWAIT
                                    """;

        String updateBookSql = """
                                UPDATE books 
                                SET available_count = available_count - 1, 
                                    borrowed_count = borrowed_count + 1 
                                WHERE id = ? AND available_count > 0
                                """;

        String insertBorrowSql = "INSERT INTO patron_book (patron_id, book_id, status) VALUES (?, ?, ?)";

        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Patron> patronCheck = patronDAO.getByName(patron.getName(), conn);
            if(patronCheck.isEmpty()) {
                return "Patron with name " + patron.getName() + " not found";
            }

            Book lockedBook = null;
            try {
                PreparedStatement selectPs = conn.prepareStatement(selectForUpdateSql);
                selectPs.setString(1, book.getName());
                selectPs.setQueryTimeout(2); // 2 second timeout

                ResultSet rs = selectPs.executeQuery();
                if(rs.next()) {
                    lockedBook = new Book();
                    lockedBook.setId(rs.getInt("book_id"));
                    lockedBook.setName(rs.getString("name"));
                    lockedBook.setNxb(rs.getInt("nxb"));
                    lockedBook.setAvailableCount(rs.getInt("available_count"));
                    lockedBook.setBorrowedCount(rs.getInt("borrowed_count"));
                } else {
                    conn.rollback();
                    return "Book with name " + book.getName() + " not found";
                }
            } catch (SQLException e) {
                conn.rollback();
                return "Book is currently being processed by another user. Please try again.";
            }

            boolean alreadyBorrowed = patronDAO.checkBorrowBook(patronCheck.get(), lockedBook, conn);
            if(!alreadyBorrowed) {
                conn.rollback();
                return "This book already borrowed by you";
            }

            PreparedStatement updatePs = conn.prepareStatement(updateBookSql);
            updatePs.setInt(1, lockedBook.getId());

            int updateResult = updatePs.executeUpdate();
            if(updateResult == 0) {
                conn.rollback();
                return "unavailable book"; // No rows affected = no available books
            }

            PreparedStatement insertPs = conn.prepareStatement(insertBorrowSql);
            insertPs.setInt(1, patronCheck.get().getId());
            insertPs.setInt(2, lockedBook.getId());
            insertPs.setString(3, "ON");

            int borrowResult = insertPs.executeUpdate();
            if(borrowResult <= 0) {
                conn.rollback();
                LOGGER.warning("Lỗi không tạo được bản ghi mượn book");
                return "Borrow book failed";
            }

            conn.commit();
            return "Borrowed book successfully";
        } catch (Exception e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
            return "Database error occurred";
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
    }
}
