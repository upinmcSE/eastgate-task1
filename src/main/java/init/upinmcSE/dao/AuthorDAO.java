package init.upinmcSE.dao;

import init.upinmcSE.model.Author;

import java.util.Optional;

public interface AuthorDAO<C> extends CrudDAO<Author, C, Integer> {
    public Optional<Author> getByName(String name, C conn) throws Exception;
}
