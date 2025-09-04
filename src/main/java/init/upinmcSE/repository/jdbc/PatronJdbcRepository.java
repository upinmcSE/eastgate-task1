package init.upinmcSE.repository.jdbc;

import init.upinmcSE.dao.PatronDAO;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatronJdbcRepository implements PatronDAO {
    private static final PatronJdbcRepository INSTANCE = new PatronJdbcRepository();

    public PatronJdbcRepository() {}

    public static PatronJdbcRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Patron> getByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM patrons WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Patron patron = new Patron();
                    patron.setId(rs.getInt("patron_id"));
                    patron.setName(rs.getString("name"));
                    patron.setAge(rs.getInt("age"));
                    return Optional.of(patron);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Patron insertOne(Patron object, Connection conn) throws SQLException {
        String sql = "INSERT INTO patrons (name, age) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
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
    public Patron updateOne(Patron object, Connection conn) throws SQLException {
        return null;
    }

    @Override
    public void deleteOne(Integer integer, Connection conn) throws SQLException {

    }

    @Override
    public boolean checkBorrowBook(Patron patron, Book book, Connection conn) throws SQLException {
        boolean result = false;
        String sql = "SELECT * FROM patron_book WHERE patron_id = ? AND book_id = ? AND status = 'ON'";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patron.getId());
            ps.setInt(2, book.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public List<Patron> getAll(Connection conn) throws SQLException {
        List<Patron> patrons = new ArrayList<>();
        String sql = "SELECT * FROM patrons";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Patron patron = new Patron();
                patron.setId(rs.getInt("patron_id"));
                patron.setName(rs.getString("name"));
                patron.setAge(rs.getInt("age"));
                patrons.add(patron);
            }
        }
        return patrons;
    }

}
