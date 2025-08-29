package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO implements DAOInterface<Book> {

    public static BookDAO getInstance() { return new BookDAO(); }

    @Override
    public int insertOne(Book object, Connection conn) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO books (name, nxb) VALUES(?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, object.getName());
            ps.setInt(2, object.getNxb());

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()) {
                        result = rs.getInt(1);
                    }
                }
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }

    @Override
    public Book getByName(String name, Connection conn) throws SQLException {
        Book book = null;
        String sql = "SELECT * FROM books WHERE name = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                book = new Book();
                book.setId(rs.getInt("book_id"));
                book.setName(rs.getString("name"));
                book.setNxb(rs.getInt("nxb"));
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
        return book;
    }

    @Override
    public int updateOne(Book object, Connection conn) throws SQLException {
        int result = 0;
        return result;
    }

    @Override
    public int deleteOne(String name, Connection conn) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM books WHERE name = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);
            result = ps.executeUpdate();
        }

        return result;
    }

    @Override
    public List<Book> getAll(Connection conn) throws SQLException {
        List<Book> books = new ArrayList<Book>();
        String sql = "SELECT * FROM books";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("book_id"));
                book.setName(rs.getString("name"));
                book.setNxb(rs.getInt("nxb"));
                books.add(book);
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
        return books;
    }

    @Override
    public List<Book> getByCondition(Connection conn) throws SQLException {
        return List.of();
    }
}
