package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AuthorService {
    private AuthorDAO authorDAO;
    private final String NOTI = "Thêm mới tác giả thất bại";

    public static AuthorService getInstance() { return new AuthorService(); }

    public String insert(Author author) {
        String result = NOTI;
        authorDAO = AuthorDAO.getInstance();
        Connection conn = null;
        try{
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Author a = authorDAO.getByName(author.getName(), conn);

            if(!Objects.isNull(a)){
                conn.rollback();
                result = "Author đã tồn tại";
                return result;
            }

            int authorId = authorDAO.insertOne(author, conn);
            conn.commit();
            return "Thêm mới thành công author id " + authorId;
        }catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return result;
    }

    public void getBookByName(String name){
        authorDAO = AuthorDAO.getInstance();
        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            Author author = authorDAO.getByName(name, conn);
            if(Objects.isNull(author)){
                System.out.println("Không tồn tại author với tên " + name);
            }else{
                System.out.println(author);
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void getAllAuthors(){
        authorDAO = AuthorDAO.getInstance();
        List<Author> authors = null;

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            authors = authorDAO.getAll(conn);

            if(Objects.isNull(authors)){
                System.out.println("Không có authors nào");
            }else{
                for(Author author : authors){
                    System.out.println(author);
                }
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void deleteAuthor(String name){
        authorDAO = AuthorDAO.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            Author author = authorDAO.getByName(name, conn);
            if(Objects.isNull(author)){
                System.out.println("Không tồn tại author với tên " + name);
            }

            int result = authorDAO.deleteOne(name, conn);
            if(result <= 0){
                System.out.println("Xóa author " + name + " không thành công");
            }else{
                System.out.println("Xóa author " + name + " thành công");
            }

        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }
}
