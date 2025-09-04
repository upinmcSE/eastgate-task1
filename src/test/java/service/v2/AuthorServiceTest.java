package service.v2;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.HibernateUtil;
import init.upinmcSE.model.Author;
import init.upinmcSE.service.v2.AuthorService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    private AuthorDAO authorDAO;
    private AuthorService authorService;
    private Session session;
    private Transaction tx;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        authorDAO = mock(AuthorDAO.class);
        authorService = new AuthorService(authorDAO);
        session = mock(Session.class);
        tx = mock(Transaction.class);
        sessionFactory = mock(SessionFactory.class);
    }

    @Test
    void testInsert_NullAuthor() {
        Integer result = authorService.insert(null);
        assertEquals(0, result);
    }

    @Test
    void testInsert_ExistingAuthor() throws Exception {
        Author existing = new Author(1, "Author1", 40);

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(tx);

            when(authorDAO.getByName(eq("Author1"), eq(session)))
                    .thenReturn(Optional.of(existing));

            Integer result = authorService.insert(existing);

            assertEquals(1, result);
            verify(tx, never()).commit();
        }
    }

    @Test
    void testInsert_NewAuthor_Success() throws Exception {
        Author newAuthor = new Author(2, "Author2", 30);
        Author inserted = new Author(2, "Author2", 30);

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(tx);

            when(authorDAO.getByName(eq("Author2"), eq(session)))
                    .thenReturn(Optional.empty());
            when(authorDAO.insertOne(eq(newAuthor), eq(session)))
                    .thenReturn(inserted);

            Integer result = authorService.insert(newAuthor);

            assertEquals(2, result);
            verify(tx).commit();
        }
    }

    @Test
    void testInsert_Exception_Rollback() throws Exception {
        Author author = new Author(1, "Author3", 45);

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(tx);

            when(authorDAO.getByName(eq("Author3"), eq(session)))
                    .thenThrow(new RuntimeException("DB error"));

            Integer result = authorService.insert(author);

            assertEquals(0, result);
            verify(tx).rollback();
        }
    }

    @Test
    void testGetAuthorByName_Found() throws Exception {
        Author author = new Author(1, "AuthorX", 50);

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getByName("AuthorX", session))
                    .thenReturn(Optional.of(author));

            Optional<Author> result = authorService.getAuthorByName("AuthorX");

            assertTrue(result.isPresent());
            assertEquals(1, result.get().getId());
        }
    }

    @Test
    void testGetAuthorByName_NotFound() throws Exception {
        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getByName("AuthorY", session))
                    .thenReturn(Optional.empty());

            Optional<Author> result = authorService.getAuthorByName("AuthorY");

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testGetAllAuthors_Success() throws Exception {
        List<Author> authors = List.of(new Author(1, "A", 30), new Author(2, "B", 40));

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getAll(session)).thenReturn(authors);

            List<Author> result = authorService.getAllAuthors();

            assertEquals(2, result.size());
        }
    }

    @Test
    void testGetAllAuthors_Exception() throws Exception {
        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getAll(session)).thenThrow(new RuntimeException("DB error"));

            List<Author> result = authorService.getAllAuthors();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testDeleteAuthor_Found() throws Exception {
        Author author = new Author(10, "DelAuthor", 60);

        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getByName(eq("DelAuthor"), eq(session)))
                    .thenReturn(Optional.of(author));

            authorService.deleteAuthor("DelAuthor");

            verify(authorDAO, times(1)).deleteOne(any(), eq(session));
        }
    }

    @Test
    void testDeleteAuthor_NotFound() throws Exception {
        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getByName("NotExist", session))
                    .thenReturn(Optional.empty());

            authorService.deleteAuthor("NotExist");

            verify(authorDAO, never()).deleteOne(any(), eq(session));
        }
    }

    @Test
    void testDeleteAuthor_Exception() throws Exception {
        try (MockedStatic<HibernateUtil> mocked = mockStatic(HibernateUtil.class)) {
            mocked.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
            when(sessionFactory.openSession()).thenReturn(session);

            when(authorDAO.getByName("Error", session))
                    .thenThrow(new RuntimeException("DB error"));

            assertDoesNotThrow(() -> authorService.deleteAuthor("Error"));
        }
    }
}
