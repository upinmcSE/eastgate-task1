package init.upinmcSE.service.v1;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;
import init.upinmcSE.repository.jdbc.AuthorJdbcRepository;
import init.upinmcSE.repository.jdbc.BookJdbcRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());
    private static final BookService INSTANCE = new BookService(BookJdbcRepository.getInstance(), AuthorJdbcRepository.getInstance() );
    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;

    public BookService(BookDAO bookDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
    }

    public static BookService getInstance() {
        return INSTANCE;
    }

    public Integer insertBook(Book book) {
        if(Objects.isNull(book)) {
            LOGGER.warning("Book is null");
            return 0;
        }
        int result = 0;
        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Book> bookSearch = bookDAO.getByName(book.getName(), conn);
            int bookId;
            if (bookSearch.isPresent()) {
                conn.rollback();
                LOGGER.info("Book exists");
                return bookSearch.get().getId();
            } else {
                bookId = bookDAO.insertOne(book, conn).getId();
            }

            if (bookId == 0) {
                conn.rollback();
                LOGGER.warning("Add new book failed");
                return result;
            }

            for (Author author : book.getAuthors()) {
                int authorId;
                Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), conn);

                if (existingAuthor.isPresent()) {
                    authorId = existingAuthor.get().getId();
                } else {
                    authorId = authorDAO.insertOne(author, conn).getId();
                }

                if (authorId == 0) {
                    conn.rollback();
                    LOGGER.warning("Add author failed");
                    return result;
                }

                int relationResult = bookDAO.insertRelation(bookId, authorId, conn);
                if (relationResult == 0) {
                    conn.rollback();
                    LOGGER.warning("Add relative book-author failed");
                    return result;
                }
            }

            conn.commit();
            result = bookId;
        } catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
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
        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Book> book = bookDAO.getByName(name, conn);
            if (book.isEmpty()) {
                conn.rollback();
                LOGGER.warning("Book with name: " + name + " not exist");
                return false;
            }

            bookDAO.deleteOne(book.get().getId(), conn);
            conn.commit();
            return true;
        } catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
            return false;
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
    }

    public List<Book> getBookByAuthor(Author author) {
        List<Book> books = new ArrayList<>();
        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            // check author exist
            Optional<Author> checkAuthor = authorDAO.getByName(author.getName(), conn);
            if (checkAuthor.isEmpty()) {
                conn.rollback();
                LOGGER.warning("Author not found");
                return List.of();
            }

            // get books by author
            books = bookDAO.getBookByAuthor(checkAuthor.get().getId(), conn);
            if (books.isEmpty()) {
                conn.commit();
                LOGGER.warning("Book not found");
                return List.of();
            }

            conn.commit();
            return books;
        }catch (SQLException e){
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return books;
    }
}