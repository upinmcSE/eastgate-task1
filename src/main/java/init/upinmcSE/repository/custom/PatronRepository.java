package init.upinmcSE.repository.custom;

import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.repository.CrudRepository;

import java.util.Optional;

public interface PatronRepository<C> extends CrudRepository<Patron, C> {
    Optional<Patron> getByName(String name, C conn) throws Exception;
    boolean checkBorrowBook(Patron patron, Book book, C conn) throws Exception;
}
