package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAO implements DAOInterface<Book> {
    private static final BookDAO INSTANCE = new BookDAO();

    private BookDAO() {}

    public static BookDAO getInstance() {
        return INSTANCE;
    }

    @Override
    public int insertOne(Book object, Connection conn) throws SQLException {
        String sql = "INSERT INTO books (name, nxb) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getNxb());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public Optional<Book> getByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM books WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("book_id"));
                    book.setName(rs.getString("name"));
                    book.setNxb(rs.getInt("nxb"));
                    return Optional.of(book);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int updateOne(Book object, Connection conn) throws SQLException {
        String sql = "UPDATE books SET name = ?, nxb = ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getNxb());
            ps.setInt(3, object.getId());
            return ps.executeUpdate();
        }
    }

    @Override
    public int deleteOne(String name, Connection conn) throws SQLException {
        String sql = "DELETE FROM books WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<Book> getAll(Connection conn) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("book_id"));
                book.setName(rs.getString("name"));
                book.setNxb(rs.getInt("nxb"));
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public List<Book> getByCondition(Connection conn) throws SQLException {
        return List.of();
    }
}
