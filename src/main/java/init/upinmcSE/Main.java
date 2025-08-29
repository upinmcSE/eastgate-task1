package init.upinmcSE;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.service.AuthorService;
import init.upinmcSE.service.BookService;
import init.upinmcSE.service.PatronService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /* 1. thêm author */
        Author author1 = new Author("C", 1993);
        //System.out.println(AuthorService.getInstance().insert(author1));


        /* 2. thêm book */
        Author author2 = new Author("B", 1990);
        Book book1 = new Book("Learn C++", 1995, List.of(author1, author2));
        //System.out.println(BookService.getInstance().insertBook(book1));

        /* 3. getAuthor by name */
        //AuthorService.getInstance().getBookByName("D");

        /* 4. getBook by name */
        //BookService.getInstance().getBookByName("Learn JAVA");

        /* 5. getAll books*/
        //BookService.getInstance().getAllBooks();

        /* 6. getAll authors*/
        //AuthorService.getInstance().getAllAuthors();

        /* 7. delete book by name*/
        //BookService.getInstance().deleteBook("Learn C#");

        /* 8. delete author by name*/
        //AuthorService.getInstance().deleteAuthor("D");

        /* 9. update author */

        /* 10. update book */

        /* 11. thêm patron*/
        Patron patron1 = new Patron("Thanh", 22);
        //System.out.println(PatronService.getInstance().addPatron(patron1));

        /* 12. get patron by name*/
        //PatronService.getInstance().getPatronByName(patron1.getName());

        /* 13. getAll patron*/
        //PatronService.getInstance().getAllPatrons();

        /* 12. xóa patron*/

        /* 13. update patron*/

        /* 14. mượn sách */
        PatronService.getInstance().borrowBook(patron1, book1);

        /* 15. trả sách */

        /*16. danh sách mượn sách */

    }
}