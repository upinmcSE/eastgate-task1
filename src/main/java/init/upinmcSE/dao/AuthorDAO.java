package init.upinmcSE.dao;

import init.upinmcSE.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDAO implements DAOInterface<Author> {
    private static final AuthorDAO INSTANCE = new AuthorDAO();

    private AuthorDAO() {}

    public static AuthorDAO getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Author> getByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM authors WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Author author = new Author();
                    author.setId(rs.getInt("author_id"));
                    author.setName(rs.getString("name"));
                    author.setAge(rs.getInt("age"));
                    return Optional.of(author);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int insertOne(Author object, Connection conn) throws SQLException {
        String sql = "INSERT INTO authors (name, age) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // author_id
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int updateOne(Author object, Connection conn) throws SQLException {
        String sql = "UPDATE authors SET name = ?, age = ? WHERE author_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());
            ps.setInt(3, object.getId());
            return ps.executeUpdate();
        }
    }

    @Override
    public int deleteOne(String name, Connection conn) throws SQLException {
        String sql = "DELETE FROM authors WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<Author> getAll(Connection conn) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Author author = new Author();
                author.setId(rs.getInt("author_id"));
                author.setName(rs.getString("name"));
                author.setAge(rs.getInt("age"));
                authors.add(author);
            }
        }
        return authors;
    }

    @Override
    public List<Author> getByCondition(Connection conn) throws SQLException {
        return List.of();
    }
}
