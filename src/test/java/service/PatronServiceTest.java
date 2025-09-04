package service;

import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.dao.PatronDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.service.v1.PatronService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PatronServiceTest {
    @Mock
    private PatronDAO patronDAO;

    @Mock
    private BookDAO bookDAO;

    @Mock
    private JDBCUtil jdbcUtil;

    @Mock
    private Connection conn;

    private PatronService patronService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patronService = new PatronService(patronDAO, bookDAO);

        MockedStatic<JDBCUtil> jdbcMock = Mockito.mockStatic(JDBCUtil.class);
        JDBCUtil jdbcUtil = mock(JDBCUtil.class);
        jdbcMock.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);
        when(jdbcUtil.getConnection()).thenReturn(conn);
    }

    @Test
    void testInsertPatron_Success() throws SQLException {
        Patron patron = new Patron(1, "A", 22);

        when(patronDAO.getByName(eq("A"), any(Connection.class)))
                .thenReturn(Optional.empty());
        when(patronDAO.insertOne(eq(patron), any(Connection.class)))
                .thenReturn(patron);

        int result = patronService.insertPatron(patron);

        assertEquals(patron.getId(), result);

        verify(patronDAO, times(1)).getByName(eq("A"), any(Connection.class));
        verify(patronDAO, times(1)).insertOne(eq(patron), any(Connection.class));
    }

    @Test
    void testInsertPatron_Failure() throws SQLException {
        Patron patron = new Patron(1, "A", 22);

        when(patronDAO.getByName(eq("A"), any(Connection.class)))
                .thenReturn(Optional.of(patron));

        int result = patronService.insertPatron(patron);

        assertEquals(0, result);
        verify(patronDAO, never()).insertOne(any(), any());
    }

    @Test
    void testInsertPatron_Null() throws SQLException {
        int result = patronService.insertPatron(null);
        assertEquals(0, result);

        verify(patronDAO, never()).getByName(any(), any());
        verify(patronDAO, never()).insertOne(any(), any());
    }

    @Test
    void testGetPatronByName_Success() throws SQLException {
        Patron patron = new Patron(1, "A", 22);
        when(patronDAO.getByName(eq("A"), any(Connection.class)))
                .thenReturn(Optional.of(patron));

        Optional<Patron> result = patronService.getPatronByName("A");
        assertEquals(patron, result.get());
        verify(patronDAO, times(1)).getByName(eq("A"), any(Connection.class));
    }

    @Test
    void testGetPatronByName_NotFound() throws SQLException {
        Patron patron = new Patron(1, "A", 22);
        when(patronDAO.getByName(eq("A"), any(Connection.class)))
            .thenReturn(Optional.empty());

        Optional<Patron> result = patronService.getPatronByName("A");
        assertTrue(result.isEmpty());
        verify(patronDAO, times(1)).getByName(eq("A"), any(Connection.class));
    }

    @Test
    void testBorrowBook_Success() throws SQLException {
        Patron patron = new Patron(1, "PatronA", 25);
        Book book = new Book(10, "BookA", 2003, 8, 2, 0, List.of());

        try (MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)) {
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection connMock = mock(Connection.class);
            PreparedStatement psMock = mock(PreparedStatement.class);

            when(jdbcUtil.getConnection()).thenReturn(connMock);
            when(connMock.prepareStatement(anyString())).thenReturn(psMock);

            when(bookDAO.getByName(eq("BookA"), eq(connMock)))
                    .thenReturn(Optional.of(book));

            when(patronDAO.getByName(eq("PatronA"), eq(connMock)))
                    .thenReturn(Optional.of(patron));

            when(patronDAO.checkBorrowBook(eq(patron), eq(book), eq(connMock)))
                    .thenReturn(true);

            when(bookDAO.checkAvailability(eq(book.getId()), eq(connMock)))
                    .thenReturn(true);

            when(psMock.executeUpdate()).thenReturn(1);

            String result = patronService.borrowBook(patron, book);

            assertEquals("Borrowed book successfully", result);

            verify(bookDAO, times(1)).getByName(eq("BookA"), eq(connMock));
            verify(patronDAO, times(1)).getByName(eq("PatronA"), eq(connMock));
            verify(patronDAO, times(1)).checkBorrowBook(eq(patron), eq(book), eq(connMock));
            verify(bookDAO, times(1)).checkAvailability(eq(book.getId()), eq(connMock));
            verify(psMock, times(1)).executeUpdate();
            verify(bookDAO, times(1)).updateOne(eq(book), eq(connMock));
            verify(connMock, times(1)).commit();

            assertEquals(7, book.getAvailableCount());
            assertEquals(3, book.getBorrowedCount());
        }
    }

    @Test
    void testBorrowBook_BookNotFound() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 1, 0, 0, List.of());

        when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.empty());

        String result = patronService.borrowBook(patron, book);

        assertEquals("Book with name BookA not found", result);

        verify(bookDAO, times(1)).getByName(any(), any());
        verify(patronDAO, never()).getByName(any(), any());
        verify(bookDAO, never()).updateOne(any(), any());
    }

    @Test
    void testBorrowBook_PatronNotFound() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 1, 0, 0, List.of());

        when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.of(book));
        when(patronDAO.getByName(eq("A"), any())).thenReturn(Optional.empty());

        String result = patronService.borrowBook(patron, book);

        assertEquals("Patron with name A not found", result);

        verify(bookDAO, times(1)).getByName(any(), any());
        verify(patronDAO, times(1)).getByName(any(), any());

        verify(bookDAO, never()).checkAvailability(eq(1), any());
    }

    @Test
    void testBorrowBook_NotAvailable() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 2, 1, 0, List.of());

        when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.of(book));
        when(patronDAO.getByName(eq("A"), any())).thenReturn(Optional.of(patron));
        when(patronDAO.checkBorrowBook(any(), any(), any())).thenReturn(true);
        when(bookDAO.checkAvailability(eq(1), any())).thenReturn(false);

        String result = patronService.borrowBook(patron, book);

        assertEquals("unavailable book", result);

        verify(bookDAO, never()).updateOne(any(), any());
    }

    @Test
    void testBorrowBook_OnBorrowed() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 1, 0, 0, List.of());

        when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.of(book));
        when(patronDAO.getByName(eq("A"), any())).thenReturn(Optional.of(patron));
        when(patronDAO.checkBorrowBook(eq(patron), eq(book), any())).thenReturn(false);

        String result = patronService.borrowBook(patron, book);

        assertEquals("This book borrowed by you", result);

        verify(bookDAO, times(1)).getByName(eq("BookA"), any());
        verify(patronDAO, times(1)).getByName(eq("A"), any());
        verify(patronDAO, times(1)).checkBorrowBook(any(), any(), any());
        verify(bookDAO, never()).checkAvailability(eq(1), any());
    }

    @Test
    void testBorrowBook_SQLException() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 1, 0, 0, List.of());

        try(MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)){
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection mockConnection = mock(Connection.class);
            when(jdbcUtil.getConnection()).thenReturn(mockConnection);

            when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.of(book));
            when(patronDAO.getByName(eq("A"), any())).thenReturn(Optional.of(patron));

            when(patronDAO.checkBorrowBook(eq(patron), eq(book), eq(mockConnection)))
                    .thenThrow(new SQLException("sql error"));

            String result = patronService.borrowBook(patron, book);

            assertEquals("Borrowed book failed", result);
            verify(jdbcUtil).rollback(mockConnection);
            verify(bookDAO, times(1)).getByName(eq("BookA"), any());
            verify(patronDAO, times(1)).getByName(eq("A"), any());
            verify(patronDAO, times(1)).checkBorrowBook(any(), any(), any());
            verify(bookDAO, never()).checkAvailability(eq(1), any());
        }
    }

    @Test
    void testReturnBook_Success() throws SQLException {
        // Arrange
        Patron patron = new Patron(1, "PatronA", 25);
        Book book = new Book(10, "BookA", 2003, 8, 2, 0, List.of());

        try (MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)) {
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection connMock = mock(Connection.class);
            PreparedStatement psMock = mock(PreparedStatement.class);

            when(jdbcUtil.getConnection()).thenReturn(connMock);
            when(connMock.prepareStatement(anyString())).thenReturn(psMock);

            when(bookDAO.getByName(eq("BookA"), eq(connMock)))
                    .thenReturn(Optional.of(book));

            when(patronDAO.getByName(eq("PatronA"), eq(connMock)))
                    .thenReturn(Optional.of(patron));

            when(psMock.executeUpdate()).thenReturn(1);

            String result = patronService.returnBook(patron, book);

            assertEquals("Return successfully", result);

            verify(bookDAO, times(1)).getByName(eq("BookA"), eq(connMock));
            verify(patronDAO, times(1)).getByName(eq("PatronA"), eq(connMock));
            verify(psMock, times(1)).executeUpdate();
            verify(bookDAO, times(1)).updateOne(eq(book), eq(connMock));
            verify(connMock, times(1)).commit();


            assertEquals(9, book.getAvailableCount());
            assertEquals(1, book.getBorrowedCount());
        }
    }


    @Test
    void testReturnBook_NotBorrowed() throws SQLException {
        Patron patron = new Patron(1, "A", 20);
        Book book = new Book(1, "BookA", 2020, 0, 1, 0, List.of());

        when(bookDAO.getByName(eq("BookA"), any())).thenReturn(Optional.of(book));
        when(patronDAO.getByName(eq("A"), any())).thenReturn(Optional.of(patron));
        when(patronDAO.checkBorrowBook(eq(patron), any(), any())).thenReturn(false);

        String result = patronService.returnBook(patron, book);

        assertEquals("Cannot find debit note for return", result);

        verify(bookDAO, never()).updateOne(any(), any());
    }
}

