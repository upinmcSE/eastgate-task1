package service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.model.Book;
import init.upinmcSE.service.v1.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private AuthorDAO authorDAO;

    @Mock
    private JDBCUtil jdbcUtil;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookDAO, authorDAO);
    }

    @Test
    void testInsertBook_NewBook_NewAuthor_Success() throws SQLException {
        Author author = new Author(1, "Author1", 30);
        Book newBook = new Book(10, "BookA", 2003, 10, 0, List.of(author));

        when(bookDAO.getByName(eq("BookA"), any(Connection.class)))
                .thenReturn(Optional.empty());

        when(bookDAO.insertOne(eq(newBook), any(Connection.class)))
                .thenReturn(newBook);

        when(authorDAO.getByName(eq("Author1"), any(Connection.class)))
                .thenReturn(Optional.empty());

        when(authorDAO.insertOne(eq(author), any(Connection.class)))
                .thenReturn(author);

        when(bookDAO.insertRelation(eq(10), eq(1), any(Connection.class)))
                .thenReturn(1);

        int result = bookService.insertBook(newBook);

        assertEquals(10, result);
        verify(bookDAO).insertOne(eq(newBook), any(Connection.class));
        verify(authorDAO).insertOne(eq(author), any(Connection.class));
        verify(bookDAO).insertRelation(eq(10), eq(1), any(Connection.class));
    }

    @Test
    void testInsertBook_BookAlreadyExists() throws SQLException {
        Book existingBook = new Book(5, "BookB", 2002, 10, 0, List.of());

        when(bookDAO.getByName(eq("BookB"), any(Connection.class)))
                .thenReturn(Optional.of(existingBook));

        int result = bookService.insertBook(existingBook);

        assertEquals(5, result);
        verify(bookDAO, never()).insertOne(any(), any());
    }

    @Test
    void testInsertBook_AuthorAlreadyExists() throws SQLException {
        Author existingAuthor = new Author(2, "Author2", 40);
        Book newBook = new Book(20, "BookC", 2004, 10, 0, List.of(existingAuthor));

        when(bookDAO.getByName(eq("BookC"), any(Connection.class)))
                .thenReturn(Optional.empty());
        when(bookDAO.insertOne(eq(newBook), any(Connection.class)))
                .thenReturn(newBook);
        when(authorDAO.getByName(eq("Author2"), any(Connection.class)))
                .thenReturn(Optional.of(existingAuthor));
        when(bookDAO.insertRelation(eq(20), eq(2), any(Connection.class)))
                .thenReturn(1);

        int result = bookService.insertBook(newBook);

        assertEquals(20, result);
        verify(authorDAO, never()).insertOne(any(), any());
        verify(bookDAO).insertRelation(eq(20), eq(2), any(Connection.class));
    }

    @Test
    void testInsertBook_Author_NullInput() throws SQLException {
        int result = bookService.insertBook(null);

        assertEquals(0, result);

        verify(bookDAO, never()).getByName(any(), any());
        verify(authorDAO, never()).insertOne(any(), any());
        verify(bookDAO, never()).insertOne(any(), any());
    }

    @Test
    void testInsertBook_SQLException() throws SQLException {
        Author author = new Author(1, "Author1", 30);
        Book book = new Book(10, "BookA", 2003, 8, 2, List.of(author));

        try(MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)) {
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection connMock = mock(Connection.class);
            when(jdbcUtil.getConnection()).thenReturn(connMock);

            when(bookDAO.getByName(eq("BookA"), eq(connMock)))
                    .thenReturn(Optional.empty());

            when(bookDAO.insertOne(eq(book), eq(connMock)))
                    .thenReturn(book);

            when(authorDAO.getByName(eq("Author1"), eq(connMock)))
                    .thenReturn(Optional.empty());

            when(authorDAO.insertOne(eq(author), eq(connMock)))
                    .thenThrow(new SQLException("sql error"));

            int result = bookService.insertBook(book);
            assertEquals(0, result);

            verify(jdbcUtil).rollback(connMock);
            verify(bookDAO, times(1)).getByName(eq("BookA"), eq(connMock));
            verify(bookDAO, times(1)).insertOne(eq(book), eq(connMock));
            verify(authorDAO, times(1)).getByName(eq("Author1"), eq(connMock));
            verify(bookDAO, never()).insertRelation(eq(book.getId()), eq(author.getId()), eq(connMock));
        }
    }

    @Test
    void testGetBookByName_NotFound() throws SQLException {
//        Book book = new Book( "BookD", 2005, 10, 0, List.of());

        when(bookDAO.getByName(eq("BookD"), any(Connection.class)))
                .thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookByName("BookD");

        assertFalse(result.isPresent());
        verify(bookDAO, times(1)).getByName(eq("BookD"), any(Connection.class));
    }

    @Test
    void testGetBookByName_Found() throws SQLException {
        Book book = new Book( "BookD", 2005, 10, 0, List.of());

        when(bookDAO.getByName(eq("BookD"), any(Connection.class)))
                .thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookByName("BookD");

        assertTrue(result.isPresent());
        assertEquals("BookD", result.get().getName());
    }

    @Test
    void testGetAllBooks() throws SQLException {
        List<Book> books = List.of(
                new Book("Book1", 2003,10, 0, List.of()),
                new Book("Book2", 2004, 10, 0, List.of())
        );

        when(bookDAO.getAll(any(Connection.class))).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Book1", result.get(0).getName());
    }

    @Test
    void testDeleteBook_NotFound() throws SQLException {
        when(bookDAO.getByName(eq("GhostBook"), any(Connection.class)))
                .thenReturn(Optional.empty());

        boolean deleted = bookService.deleteBook("GhostBook");

        assertFalse(deleted);
        verify(bookDAO, never()).deleteOne(any(), any());
    }

    @Test
    void testDeleteBook_Found() throws SQLException {
        Book book = new Book(99,"RealBook", 1999, 10, 0, List.of());

        when(bookDAO.getByName(eq("RealBook"), any(Connection.class)))
                .thenReturn(Optional.of(book));

        boolean deleted = bookService.deleteBook("RealBook");

        assertTrue(deleted);
        verify(bookDAO).deleteOne(eq(99), any(Connection.class));
    }

    @Test
    void testGetBookByAuthor_AuthorNotFound() throws SQLException {
        Author inputAuthor = new Author(1, "Unknown", 40);

        when(authorDAO.getByName(eq("Unknown"), any(Connection.class)))
                .thenReturn(Optional.empty());

        List<Book> result = bookService.getBookByAuthor(inputAuthor);

        assertTrue(result.isEmpty());
        verify(bookDAO, never()).getBookByAuthor(eq(1), any(Connection.class));
    }

    @Test
    void testGetBookByAuthor_BooksNotFound() throws SQLException {
        Author inputAuthor = new Author(1, "AuthorA", 50);
        Author dbAuthor = new Author(1, "AuthorA", 50);

        when(authorDAO.getByName(eq("AuthorA"), any(Connection.class)))
                .thenReturn(Optional.of(dbAuthor));
        when(bookDAO.getBookByAuthor(eq(1), any(Connection.class)))
                .thenReturn(List.of());

        List<Book> result = bookService.getBookByAuthor(inputAuthor);

        assertTrue(result.isEmpty());

        verify(authorDAO, times(1)).getByName(eq("AuthorA"), any(Connection.class));
    }

    @Test
    void testGetBookByAuthor_FoundBooks() throws SQLException {
        Author author = new Author(1, "AuthorB", 60);
        Book book = new Book(1, "Book1", 2000,1, 0, List.of(author));

        Book book2 = new Book(2, "Book2", 2000,1, 0, List.of(author));

        when(authorDAO.getByName(eq("AuthorB"), any(Connection.class)))
                .thenReturn(Optional.of(author));
        when(bookDAO.getBookByAuthor(eq(1), any(Connection.class)))
                .thenReturn(List.of(book, book2));

        List<Book> result = bookService.getBookByAuthor(author);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Book1", result.get(0).getName());
    }

    @Test
    void testGetBookByAuthor_SQLException() throws SQLException {
        Author inputAuthor = new Author(1, "AuthorC", 70);

        try (MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)) {
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection mockConnection = mock(Connection.class);
            when(jdbcUtil.getConnection()).thenReturn(mockConnection);

            // Mock throw a SQLException when call method
            when(authorDAO.getByName(eq("AuthorC"), eq(mockConnection)))
                    .thenThrow(new SQLException("DB error"));

            List<Book> result = bookService.getBookByAuthor(inputAuthor);

            assertTrue(result.isEmpty());

            verify(jdbcUtil).rollback(mockConnection);
        }
    }

}
