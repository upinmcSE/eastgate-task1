package init.upinmcSE.dao;

import init.upinmcSE.model.Author;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface AuthorDAO extends CrudDAO<Author, Integer> {
    public Optional<Author> getByName(String name, Connection conn) throws SQLException;
}
