package init.upinmcSE.service.v1;

import init.upinmcSE.repository.custom.AuthorRepository;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());
    private final AuthorRepository authorDAO;

    public AuthorService(AuthorRepository authorDAO) {
        this.authorDAO = authorDAO;
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
                return existingAuthor.get().getId();
            }

            Author authorInsert = (Author) authorDAO.insertOne(author, conn);
            conn.commit();
            result = authorInsert.getId();
        } catch (Exception e) {
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
        } catch (Exception e) {
            JDBCUtil.getInstance().printSQLException(e);
            return Optional.empty();
        }
    }

    public List<Author> getAllAuthors() {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            return authorDAO.getAll(conn);
        } catch (Exception e) {
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
            authorDAO.deleteOne(author.get(), conn);
        } catch (Exception e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }
}
