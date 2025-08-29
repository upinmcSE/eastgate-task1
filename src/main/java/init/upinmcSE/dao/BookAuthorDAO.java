package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookAuthorDAO {

    public static BookAuthorDAO getInstance() { return new BookAuthorDAO(); }

    public int insertRelation(int bookID, int authorID, Connection conn) {
        int result = 0;
        String sql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, bookID);
            ps.setInt(2, authorID);
            result = ps.executeUpdate();

        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }
}
