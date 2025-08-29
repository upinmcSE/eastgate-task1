package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.*;
import java.util.List;

public class AuthorDAO implements DAOInterface<Author> {

    @Override
    public Author getByName(String name, Connection conn) throws SQLException {
        Author author = null;
        String sql = "SELECT * FROM authors WHERE name = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                author = new Author();
                author.setId(rs.getInt("author_id"));
                author.setName(rs.getString("name"));
                author.setAge(rs.getInt("age"));
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return author;
    }

    public static AuthorDAO getInstance() {return new AuthorDAO();}

    @Override
    public int insertOne(Author object, Connection conn) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO authors (name, age) VALUES(?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());

            result = ps.executeUpdate();
            if(result > 0){
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    result = rs.getInt(1);
                }
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }

    @Override
    public int insertMany(List<Author> objects, Connection conn) throws SQLException {
        int result = 0;
        return result;
    }

    @Override
    public int updateOne(Author object, Connection conn) throws SQLException {
        int result = 0;
        return result;
    }

    @Override
    public int deleteOne(int id, Connection conn) throws SQLException {
        int result = 0;
        return result;
    }

    @Override
    public Author getOne(int id, Connection conn) throws SQLException {
        return null;
    }

    @Override
    public List<Author> getAll(Connection conn) throws SQLException {
        return List.of();
    }

    @Override
    public List<Author> getByCondition(Connection conn) throws SQLException {
        return List.of();
    }
}
