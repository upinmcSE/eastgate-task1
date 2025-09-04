package init.upinmcSE.repository.custom;

import init.upinmcSE.model.Author;
import init.upinmcSE.repository.CrudRepository;

import java.util.Optional;

public interface AuthorRepository<C> extends CrudRepository<Author, C> {
    Optional<Author> getByName(String name, C conn) throws Exception;
}
