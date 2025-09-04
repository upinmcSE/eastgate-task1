package init.upinmcSE.repository.custom;

import init.upinmcSE.model.Book;
import init.upinmcSE.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository<C> extends CrudRepository<Book, C> {
    Optional<Book> getByName(String name, C conn) throws Exception;
    Integer insertRelation(int bookID, int authorID, C conn) throws Exception;
    boolean checkAvailability(int bookID, C conn) throws Exception;
    List<Book> getBookByAuthor(int authorID, C conn) throws Exception;
}
