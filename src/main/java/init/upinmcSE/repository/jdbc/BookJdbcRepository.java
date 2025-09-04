package init.upinmcSE.repository.jdbc;

import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookJdbcRepository implements BookDAO {
    private static final BookJdbcRepository INSTANCE = new BookJdbcRepository();

    public BookJdbcRepository() {}

    public static BookJdbcRepository getInstance() {
        return INSTANCE;
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
    public Integer insertRelation(int bookID, int authorID, Connection conn) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, bookID);
            ps.setInt(2, authorID);
            result = ps.executeUpdate();
        }
        return result;
    }

    @Override
    public boolean checkAvailability(int bookID, Connection conn) throws SQLException {
        String sql = "SELECT available FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, bookID);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                throw new SQLException("Book does not exist");
            }

            int availableCount = rs.getInt("available_count");
            return availableCount > 0;
        }
    }

    @Override
    public Book insertOne(Book object, Connection conn) throws SQLException {
        String sql = "INSERT INTO books (name, nxb, available_count) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getNxb());
            ps.setInt(3, object.getAvailableCount());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        object.setId(rs.getInt(1));
                        return object;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Book> getBookByAuthor(int authorID, Connection conn) throws SQLException {
        List<Book> books;
        String sql = "SELECT b.book_id, b.name, b.nxb " +
                     "FROM books b " +
                     "JOIN book_author ba ON b.book_id = ba.book_id " +
                     "WHERE ba.author_id = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, authorID);

            try(ResultSet rs = ps.executeQuery()) {
                books = new ArrayList<>();
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("book_id"));
                    book.setName(rs.getString("name"));
                    book.setNxb(rs.getInt("nxb"));
                    book.setAvailableCount(rs.getInt("available_count"));
                    book.setBorrowedCount(rs.getInt("borrowed_count"));
                    books.add(book);
                }
            }
        }
        return books;
    }

    @Override
    public Book updateOne(Book object, Connection conn) throws SQLException {
        String sql = "UPDATE books SET name = ?, nxb = ?, available_count = ?, borrowed_count = ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getNxb());
            ps.setInt(3, object.getId());
            ps.setInt(4, object.getAvailableCount());
            ps.setInt(5, object.getBorrowedCount());
            return ps.executeUpdate() > 0 ? object : null;
        }
    }

    @Override
    public void deleteOne(Integer id, Connection conn) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
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
}
