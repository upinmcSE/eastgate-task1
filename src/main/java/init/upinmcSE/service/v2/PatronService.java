package init.upinmcSE.service.v2;

import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.repository.custom.BookRepository;
import init.upinmcSE.repository.custom.PatronRepository;

import java.util.Optional;
import java.util.logging.Logger;

public class PatronService {
    private static final Logger LOGGER = Logger.getLogger(PatronService.class.getName());
    private static final String NOTI = "Thêm mới độc giả thất bại";
    private PatronRepository patronDAO;
    private BookRepository bookDAO;

    public PatronService() {}

    public PatronService(PatronRepository patronDAO, BookRepository bookDAO) {
        this.patronDAO = patronDAO;
        this.bookDAO = bookDAO;
    }

    public Integer insertPatron(Patron patron) {

        return 0;
    }

    public Optional<Patron> getPatronByName(String name) {

        return Optional.empty();
    }

    public void getAllPatrons() {}

    public String borrowBook(Patron patron, Book book) {

        return "";
    }

    public String returnBook(Patron patron, Book book) {

        return "";
    }

    public String borrowBookOptimistic(Patron patron, Book book) {

        return "";
    }

    public String borrowBookPessimistic(Patron patron, Book book) {
        return "";
    }
}
