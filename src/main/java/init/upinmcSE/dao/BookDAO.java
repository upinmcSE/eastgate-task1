package init.upinmcSE.dao;

import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookDAO extends CrudDAO<Book, Integer> {
    public Optional<Book> getByName(String name, Connection conn) throws SQLException;
    public Integer insertRelation(int bookID, int authorID, Connection conn) throws SQLException;
    public boolean checkAvailability(int bookID, Connection conn) throws SQLException;
    public List<Book> getBookByAuthor(int authorID, Connection conn) throws SQLException;
}
