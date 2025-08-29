package init.upinmcSE.dao;

import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Patron;

import java.awt.print.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatronDAO implements DAOInterface<Patron> {

    public static PatronDAO getInstance() { return new PatronDAO(); }

    @Override
    public int insertOne(Patron object, Connection conn) {
        int patronId = 0;
        String sql = "INSERT INTO patrons (name, age) VALUES(?,?)";

        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, object.getName());
            ps.setInt(2, object.getAge());

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()) {
                        patronId = rs.getInt(1);
                    }
                }
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return patronId;
    }

    @Override
    public Patron getByName(String name, Connection conn) {
        Patron patron = null;
        String sql = "SELECT * FROM patrons WHERE name = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                patron = new Patron();
                patron.setId(rs.getInt("patron_id"));
                patron.setName(rs.getString("name"));
                patron.setAge(rs.getInt("age"));
            }
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return patron;
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
    public List<Patron> getAll(Connection conn) {
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
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return patrons;
    }

    @Override
    public List<Patron> getByCondition(Connection conn) {
        return List.of();
    }
}
