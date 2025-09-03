package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());
    private static final AuthorService INSTANCE = new AuthorService();
    private final AuthorDAO authorDAO = AuthorDAO.getInstance();
    private final String NOTI = "Thêm mới tác giả thất bại";

    public static AuthorService getInstance() { return new AuthorService(); }

    public String insert(Author author) {
        String result = NOTI;
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), conn);

            if (existingAuthor.isPresent()) {
                conn.rollback();
                return "Author đã tồn tại";
            }

            int authorId = authorDAO.insertOne(author, conn);
            conn.commit();
            result = "Thêm mới thành công author id " + authorId;
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
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

    public boolean deleteAuthor(String name) {
        try (Connection conn = JDBCUtil.getInstance().getConnection()) {
            Optional<Author> author = authorDAO.getByName(name, conn);
            if (author.isEmpty()) {
                LOGGER.warning("Không tồn tại author với tên " + name);
                return false;
            }
            int result = authorDAO.deleteOne(name, conn);
            return result > 0;
        } catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
            return false;
        }
    }
}
