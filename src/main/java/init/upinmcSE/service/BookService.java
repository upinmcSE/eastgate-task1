package init.upinmcSE.service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookAuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class BookService {
    private final String NOTI = "Thêm mới book thất bại";
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private BookAuthorDAO bookAuthorDAO;

    public static BookService getInstance() {
        return new BookService();
    }

    public String insertBook(Book book) {
        String result = NOTI;
        bookDAO = BookDAO.getInstance();
        authorDAO = AuthorDAO.getInstance();
        bookAuthorDAO = BookAuthorDAO.getInstance();

        Connection conn = null;
        try {
            conn = JDBCUtil.getInstance().getConnection();
            conn.setAutoCommit(false);

            Book bookSearch = bookDAO.getByName(book.getName(), conn);
            int bookId = 0;
            if(Objects.isNull(bookSearch)) {
                bookId = bookDAO.insertOne(book, conn);
            }else{
                conn.rollback();
                return "Book với tên " + book.getName() + " đã tồn tại";
            }

            if (bookId == 0) {
                conn.rollback();
                return result;
            }

            for (Author author : book.getAuthors()) {
                int authorId = 0;
                Author a = authorDAO.getByName(author.getName(), conn);

                if(Objects.isNull(a)){
                    authorId = authorDAO.insertOne(author, conn);
                }else{
                    authorId = a.getId();
                }

                if (authorId == 0) {
                    conn.rollback();
                    return result;
                }

                int relationResult = bookAuthorDAO.insertRelation(bookId, authorId, conn);
                if (relationResult == 0) {
                    conn.rollback();
                    return result;
                }
            }

            conn.commit();
            result = "Đã thêm thành công book với id: " + bookId;
        }catch (SQLException e) {
            JDBCUtil.getInstance().rollback(conn);
            JDBCUtil.getInstance().printSQLException(e);
        } finally {
            JDBCUtil.getInstance().closeConnection(conn);
        }
        return result;
    }

    public void getBookByName(String name){
        BookDAO bookDAO = BookDAO.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            Book book = bookDAO.getByName(name, conn);
            if(Objects.isNull(book)){
                System.out.println("Không tồn tại book với tên " + name);
            }else{
                System.out.println(book);
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void getAllBooks(){
        BookDAO bookDAO = BookDAO.getInstance();
        List<Book> books = null;
        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            books = bookDAO.getAll(conn);

            if(Objects.isNull(books)){
                System.out.println("Không tồn tại books nào");
            }else{
                for (Book book : books) {
                    System.out.println(book);
                };
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }

    public void deleteBook(String name){
        BookDAO bookDAO = BookDAO.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){

            Book book = bookDAO.getByName(name, conn);
            if(Objects.isNull(book)){
                System.out.println("Book với tên " + name + " không tồn tại");
                return;
            }

            int result = bookDAO.deleteOne(name, conn);
            if(result <= 0){
                System.out.println("Xóa book với name " + name + " không thành công");
            }else{
                System.out.println("Xóa book với name " + name + " thành công");
            }
        }catch (SQLException e) {
            JDBCUtil.getInstance().printSQLException(e);
        }
    }
}