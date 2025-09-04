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
    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;

    public BookService(BookDAO bookDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
    }

    private static final BookService INSTANCE = new BookService(BookJdbcRepository.getInstance(), AuthorJdbcRepository.getInstance() );
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
                LOGGER.info("Book exists");
                return bookSearch.get().getId();
            } else {
                Book insertBook = (Book) bookDAO.insertOne(book, conn);
                bookId = insertBook.getId();
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
                    Author authorInsert = (Author) authorDAO.insertOne(author, conn);
                    authorId = authorInsert.getId();
                }

                if (authorId == 0) {
                    conn.rollback();
                    LOGGER.warning("Add author failed");
                    return result;
                }
                int relationResult = bookDAO.insertRelation(bookId, authorId, conn);
                LOGGER.info(relationResult + " inserteddd");
                if (relationResult == 0) {
                    conn.rollback();
                    LOGGER.warning("Add relative book-author failed");
                    return result;
                }
            }

            conn.commit();
            result = bookId;
        } catch (Exception e) {
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
        } catch (Exception e) {
            JDBCUtil.getInstance().printSQLException(e);
            return Optional.empty();
        }
    }

    public List<Book> getAllBooks() {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return bookDAO.getAll(conn);
        } catch (Exception e) {
            JDBCUtil.getInstance().printSQLException(e);
            return List.of();
        }
    }

    public void deleteBook(String name) {
        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Book> book = bookDAO.getByName(name, conn);
            if (book.isEmpty()) {
                LOGGER.warning("Book with name: " + name + " not exist");
                return;
            }

            bookDAO.deleteOne(book.get(), conn);
            conn.commit();
        } catch (Exception e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
    }

    public List<Book> getBookByAuthor(Author author) {
        List<Book> books = new ArrayList<>();
        try(Connection conn = JDBCUtil.getInstance().getConnection()) {
            Optional<Author> checkAuthor = authorDAO.getByName(author.getName(), conn);
            if (checkAuthor.isEmpty()) {
                LOGGER.warning("Author not found");
                return books;
            }

            books = bookDAO.getBookByAuthor(checkAuthor.get().getId(), conn);
            if (books.isEmpty()) {
                LOGGER.warning("Book not found");
                return books;
            }
        }catch (Exception e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return books;
    }
}