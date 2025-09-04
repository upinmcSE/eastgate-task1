package init.upinmcSE.repository.custom.impl.jdbc;

import init.upinmcSE.repository.custom.AuthorRepository;
import init.upinmcSE.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorJdbcRepository implements AuthorRepository<Connection> {
    private static final AuthorJdbcRepository INSTANCE = new AuthorJdbcRepository();

    private AuthorJdbcRepository() {}

    public static AuthorJdbcRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Author> getByName(String name, Connection conn) throws Exception {
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
    public Author insertOne(Author object, Connection conn) throws Exception {
        String sql = "INSERT INTO authors (name, age) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());

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
    public Author updateOne(Author object, Connection conn) throws Exception {
        String sql = "UPDATE authors SET name = ?, age = ? WHERE author_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());
            ps.setInt(3, object.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0 ? object : null;
        }
    }

    @Override
    public void deleteOne(Author object, Connection conn) throws Exception {
        String sql = "DELETE FROM authors WHERE author_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, object.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Author> getAll(Connection conn) throws Exception {
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
}
