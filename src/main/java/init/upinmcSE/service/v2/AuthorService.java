package init.upinmcSE.service.v2;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.model.Author;

import java.util.logging.Logger;

public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());

    private AuthorDAO authorDAO;
    public AuthorService() {}

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public String insertAuthor(Author author) {

    }
}
