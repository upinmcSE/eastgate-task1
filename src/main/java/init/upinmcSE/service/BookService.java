package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookAuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());
    private static final BookService INSTANCE = new BookService();
    private final BookDAO bookDAO = BookDAO.getInstance();
    private final AuthorDAO authorDAO = AuthorDAO.getInstance();
    private final BookAuthorDAO bookAuthorDAO = BookAuthorDAO.getInstance();
    private final String NOTI = "Thêm mới book thất bại";

    private BookService() {}

    public static BookService getInstance() {
        return INSTANCE;
    }

    public String insertBook(Book book) {
        String result = NOTI;
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Optional<Book> bookSearch = bookDAO.getByName(book.getName(), conn);
            int bookId;
            if (bookSearch.isPresent()) {
                conn.rollback();
                return "Book với tên " + book.getName() + " đã tồn tại";
            } else {
                bookId = bookDAO.insertOne(book, conn);
            }

            if (bookId == 0) {
                conn.rollback();
                return result;
            }

            for (Author author : book.getAuthors()) {
                int authorId;
                Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), conn);

                if (existingAuthor.isPresent()) {
                    authorId = existingAuthor.get().getId();
                } else {
                    authorId = authorDAO.insertOne(author, conn);
                }

                if (authorId == 0) {
                    conn.rollback();
                    return result;
                }

                int relationResult = bookAuthorDAO.insertRelation(bookId, authorId, conn);
                if (relationResult == 0) {
                    conn.rollback();
                    return result;
                }
            }

            conn.commit();
            result = "Đã thêm thành công book với id: " + bookId;
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }

    public Optional<Book> getBookByName(String name) {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return bookDAO.getByName(name, conn);
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return Optional.empty();
        }
    }

    public List<Book> getAllBooks() {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return bookDAO.getAll(conn);
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return List.of();
        }
    }

    public boolean deleteBook(String name) {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            Optional<Book> book = bookDAO.getByName(name, conn);
            if (book.isEmpty()) {
                LOGGER.warning("Book với tên " + name + " không tồn tại");
                return false;
            }

            int result = bookDAO.deleteOne(name, conn);
            return result > 0;
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return false;
        }
    }
}