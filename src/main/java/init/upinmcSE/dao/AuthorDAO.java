package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AuthorDAO implements DAOInterface<Author> {

    @Override
    public Author getByID(int id) {
        Author author = null;
        String sql = "SELECT * FROM authors WHERE id = ?";
        try(Connection conn = JDBCUtil.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                return null;
            }

            while (rs.next()) {
                author.setId(rs.getInt("id"));
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
    public int insertOne(Author object) {
        int result = 0;
        String sql = "INSERT INTO authors (id, name, age) VALUES(?,?,?)";
        try(Connection conn = JDBCUtil.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setInt(1, object.getId());
            ps.setString(2, object.getName());
            ps.setInt(3, object.getAge());

            result = ps.executeUpdate();
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }

    @Override
    public int insertMany(List<Author> objects) {
        int result = 0;

        try(Connection conn = JDBCUtil.getInstance().getConnection()){


        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }

        return result;
    }

    @Override
    public int updateOne(Author object) {
        int result = 0;

        try(Connection conn = JDBCUtil.getInstance().getConnection()){

        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }

        return result;
    }

    @Override
    public int deleteOne(int id) {
        int result = 0;

        try(Connection conn = JDBCUtil.getInstance().getConnection()){

        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }

        return result;
    }

    @Override
    public Author getOne(int id) {
        return null;
    }

    @Override
    public List<Author> getAll() {
        return List.of();
    }

    @Override
    public List<Author> getByCondition() {
        return List.of();
    }
}
