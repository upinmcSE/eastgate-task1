package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class AuthorService {
    AuthorDAO authorDAO;

    public static AuthorService getInstance() { return new AuthorService(); }

    public int insert(Author author) {
        int result = 0;
        authorDAO = AuthorDAO.getInstance();
        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            conn.setAutoCommit(false);

            Author a = authorDAO.getByID(author.getId());

            if(!Objects.isNull(a)){
                conn.rollback();
            }

            result = authorDAO.insertOne(author);
            conn.commit();
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }
}
