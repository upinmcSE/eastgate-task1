package init.upinmcSE.service;

import init.upinmcSE.dao.BookAuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.SQLException;

public class BookService {
    private BookDAO bookDAO;
    private BookAuthorDAO bookAuthorDAO;
    private AuthorService authorService;

    public static BookService getInstance() { return new BookService(); }

    public int insertBook(Book book) {
        int result = 0;
        bookDAO = BookDAO.getInstance();
        bookAuthorDAO = BookAuthorDAO.getInstance();
        authorService = AuthorService.getInstance();

        try(Connection conn = JDBCUtil.getInstance().getConnection()){
            conn.setAutoCommit(false);

            result =  bookDAO.insertOne(book);
            if(result == 0){
                conn.rollback();
            }

            for(Author author : book.getAuthors()){
                authorService.insert(author);

                int i = bookAuthorDAO.insertRelation(author.getId(), book.getId());
                if(i == 0){
                    conn.rollback();
                }
            }

            conn.commit();
        }catch (SQLException e){
            JDBCUtil.getInstance().printSQLException(e);
        }
        return result;
    }
}
