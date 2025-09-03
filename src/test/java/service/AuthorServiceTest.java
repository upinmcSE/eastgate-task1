package service;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.JDBCUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.service.AuthorService;
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
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @Mock
    private AuthorDAO authorDAO;

    @Mock
    private JDBCUtil jdbcUtil;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorService = AuthorService.getInstance(authorDAO);
    }

    @Test
    void testInsert_NewAuthor_Success() throws SQLException {
        Author newAuthor = new Author(1, "Dương", 25);

        when(authorDAO.getByName(eq("Dương"), any(Connection.class)))
                .thenReturn(Optional.empty());
        when(authorDAO.insertOne(eq(newAuthor), any(Connection.class)))
                .thenReturn(newAuthor);

        int id = authorService.insert(newAuthor);

        assertEquals(1, id);

        verify(authorDAO).insertOne(eq(newAuthor), any(Connection.class));
    }

    @Test
    void testInsert_ExistingAuthor() throws SQLException {
        Author existing = new Author(2, "Trung", 30);

        when(authorDAO.getByName(eq("Trung"), any(Connection.class)))
                .thenReturn(Optional.of(existing));

        int id = authorService.insert(existing);

        assertEquals(2, id);

        verify(authorDAO, never()).insertOne(any(), any());
    }

    @Test
    void testInsert_NullInput() throws SQLException {
        int result = authorService.insert(null);
        assertEquals(0, result);

        verify(authorDAO, never()).getByName(any(), any());
        verify(authorDAO, never()).insertOne(any(), any());
    }

    @Test
    void testInsert_SQLException() throws SQLException {
        Author author = new Author(1, "Trung", 30);

        try(MockedStatic<JDBCUtil> mockedStatic = mockStatic(JDBCUtil.class)) {
            mockedStatic.when(JDBCUtil::getInstance).thenReturn(jdbcUtil);

            Connection mockConnection = mock(Connection.class);
            when(jdbcUtil.getConnection()).thenReturn(mockConnection);

            when(authorDAO.getByName(eq("Trung"), eq(mockConnection)))
                    .thenReturn(Optional.empty());

            when(authorDAO.insertOne(eq(author), eq(mockConnection)))
                    .thenThrow(new SQLException("sql error"));

            int result = authorService.insert(author);
            assertEquals(0, result);

            verify(jdbcUtil).rollback(mockConnection);
            verify(authorDAO, times(1)).getByName(eq("Trung"), eq(mockConnection));
            verify(authorDAO, times(1)).insertOne(any(), eq(mockConnection));
        }
    }

    @Test
    void testGetAuthorByName_Found() throws SQLException {
        Author author = new Author(3, "Thành", 28);

        when(authorDAO.getByName(eq("Thành"), any(Connection.class)))
                .thenReturn(Optional.of(author));

        Optional<Author> result = authorService.getAuthorByName("Thành");

        assertTrue(result.isPresent());
        assertEquals("Thành", result.get().getName());

        verify(authorDAO).getByName(eq(author.getName()), any(Connection.class));
    }

    @Test
    void testGetAuthorByName_NotFound() throws SQLException {
        when(authorDAO.getByName(eq("Không có"), any(Connection.class)))
                .thenReturn(Optional.empty());

        Optional<Author> result = authorService.getAuthorByName("Không có");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllAuthors_Found() throws SQLException {
        List<Author> authors = List.of(
                new Author(1, "A", 20),
                new Author(2, "B", 25)
        );

        when(authorDAO.getAll(any(Connection.class))).thenReturn(authors);

        List<Author> result = authorService.getAllAuthors();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
    }

    @Test
    void testGetAllAuthors_Empty() throws SQLException {
        when(authorDAO.getAll(any(Connection.class))).thenReturn(List.of());
        List<Author> result = authorService.getAllAuthors();
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteAuthor_NotFound() throws SQLException {
        when(authorDAO.getByName(eq("Ghost"), any(Connection.class)))
                .thenReturn(Optional.empty());

        authorService.deleteAuthor("Ghost");

        verify(authorDAO, never()).deleteOne(any(), any());
    }

    @Test
    void testDeleteAuthor_Found() throws SQLException {
        Author author = new Author(5, "Alive", 40);

        when(authorDAO.getByName(eq("Alive"), any(Connection.class)))
                .thenReturn(Optional.of(author));

        authorService.deleteAuthor("Alive");

        verify(authorDAO).deleteOne(eq(5), any(Connection.class));
    }
}
