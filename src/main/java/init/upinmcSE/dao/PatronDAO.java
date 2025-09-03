package init.upinmcSE.dao;

import init.upinmcSE.model.Patron;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatronDAO implements DAOInterface<Patron> {
    private static final PatronDAO INSTANCE = new PatronDAO();
    private PatronDAO() {}

    public static PatronDAO getInstance() {
        return INSTANCE;
    }

    @Override
    public int insertOne(Patron object, Connection conn) throws SQLException {
        String sql = "INSERT INTO patrons (name, age) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
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
    public int updateOne(Patron object, Connection conn) {
        return 0;
    }

    @Override
    public int deleteOne(String name, Connection conn) {
        return 0;
    }

    @Override
    public List<Patron> getAll(Connection conn) throws SQLException {
        List<Patron> patrons = null;
        String sql = "SELECT * FROM patrons";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();

            patrons = new ArrayList<>();
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

    @Override
    public List<Patron> getByCondition(Connection conn) {
        return List.of();
    }
}
