package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.repository.jdbc.AuthorJdbcRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());
    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    private static final AuthorService INSTANCE =
            new AuthorService(AuthorJdbcRepository.getInstance());

    public static AuthorService getInstance() {
        return INSTANCE;
    }

    public static AuthorService getInstance(AuthorDAO authorDAO) {
        return new AuthorService(authorDAO);
    }

    public Integer insert(Author author) {
        if(Objects.isNull(author)) {
            LOGGER.warning("author is null");
            return 0;
        }
        int result = 0;
        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), conn);

            if (existingAuthor.isPresent()) {
                conn.rollback();
                return existingAuthor.get().getId();
            }

            Author authorInsert = authorDAO.insertOne(author, conn);
            conn.commit();
            result = authorInsert.getId();
        } catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        }finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return result;
    }

    public Optional<Author> getAuthorByName(String name) {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return authorDAO.getByName(name, conn);
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return Optional.empty();
        }
    }

    public List<Author> getAllAuthors() {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return authorDAO.getAll(conn);
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return List.of();
        }
    }

    public void deleteAuthor(String name) {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            Optional<Author> author = authorDAO.getByName(name, conn);
            if (author.isEmpty()) {
                LOGGER.warning("Không tồn tại author với tên " + name);
                return;
            }
            authorDAO.deleteOne(author.get().getId(), conn);
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }
}
