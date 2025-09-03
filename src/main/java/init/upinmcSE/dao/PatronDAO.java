package init.upinmcSE.dao;

import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface PatronDAO extends CrudDAO<Patron, Integer> {
    public Optional<Patron> getByName(String name, Connection conn) throws SQLException;
    public boolean checkBorrowBook(Patron patron, Book book, Connection conn) throws SQLException;
}
