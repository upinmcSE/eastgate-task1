package init.upinmcSE.dao;

import init.upinmcSE.model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookDAO<C> extends CrudDAO<Book, C, Integer> {
    public Optional<Book> getByName(String name, C conn) throws Exception;
    public Integer insertRelation(int bookID, int authorID, C conn) throws Exception;
    public boolean checkAvailability(int bookID, C conn) throws Exception;
    public List<Book> getBookByAuthor(int authorID, C conn) throws Exception;
}
