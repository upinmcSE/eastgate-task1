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
        /* 1. thêm author */
        Author author1 = new Author("C", 1993);
        //System.out.println(AuthorService.getInstance().insert(author1));


        /* 2. thêm book */
        Author author2 = new Author("B", 1990);
        Book book1 = new Book("Learn C#", 1995, List.of(author1, author2));
        //System.out.println(BookService.getInstance().insertBook(book1));

        /* 3. getAuthor by name */
        //AuthorService.getInstance().getBookByName("D");

        /* 4. getBook by name */
        BookService.getInstance().getBookByName("Learn JAVA");


    }
}