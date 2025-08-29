package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BookDAO implements DAOInterface<Book> {

    public static BookDAO getInstance() { return new BookDAO(); }

    @Override
    public Book getByID(int id) {
        return null;
    }

    @Override
    public int insertOne(Book object) {
        int result = 0;
        String sql = "INSERT INTO books (id, name, nxb) VALUES(?, ?, ?)";
        try(Connection conn = JDBCUtil.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setInt(1, object.getId());
            ps.setString(2, object.getName());
            ps.setInt(3, object.getNxb());

            result = ps.executeUpdate();
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }

    private void initRelation(int bookID, int authorID){

    }

    @Override
    public int insertMany(List<Book> objects) {
        int result = 0;
        return result;
    }

    @Override
    public int updateOne(Book object) {
        int result = 0;
        return result;
    }

    @Override
    public int deleteOne(int id) {
        int result = 0;
        return result;
    }

    @Override
    public Book getOne(int id) {
        return null;
    }

    @Override
    public List<Book> getAll() {
        return List.of();
    }

    @Override
    public List<Book> getByCondition() {
        return List.of();
    }
}
