package init.upinmcSE;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;
import init.upinmcSE.service.AuthorService;
import init.upinmcSE.service.BookService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /* thêm author */
        //Author author1 = new Author(1, "A", 1990);
        //System.out.println("Đã thêm mới " + AuthorService.getInstance().insert(author1) + " bản ghi");

        /* thêm book */

        /* happy case*/
        Author author2 = new Author(2, "B", 1990);
        Author author3 = new Author(3, "C", 1990);
        Book book1 = new Book(1, "Learn JAVA", 2000, List.of(author2, author3));
        System.out.println("Đã thêm mới " + BookService.getInstance().insertBook(book1));

        /* edge case */

    }
}