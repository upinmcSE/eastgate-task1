package init.upinmcSE.dao;

import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;

import java.util.Optional;

public interface PatronDAO<C> extends CrudDAO<Patron, C, Integer> {
    public Optional<Patron> getByName(String name, C conn) throws Exception;
    public boolean checkBorrowBook(Patron patron, Book book, C conn) throws Exception;
}
