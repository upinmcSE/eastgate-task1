package init.upinmcSE.service.v2;

import init.upinmcSE.dao.AuthorDAO;
import init.upinmcSE.db.HibernateUtil;
import init.upinmcSE.model.Author;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class.getName());

    private AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public Integer insert(Author author) {
        if(Objects.isNull(author)) {
            LOGGER.warning("author is null");
            return 0;
        }
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            Optional<Author> existingAuthor = authorDAO.getByName(author.getName(), session);
            if(existingAuthor.isPresent()) {
               return existingAuthor.get().getId();
            }

            Author insertAuthor = (Author) authorDAO.insertOne(author, session);
            tx.commit();
            return insertAuthor.getId();
        }catch (Exception e){
            if (tx != null) tx.rollback();
            HibernateUtil.printHibernateException(e);
        }
        return 0;
    }

    public Optional<Author> getAuthorByName(String name){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return authorDAO.getByName(name, session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return Optional.empty();
    }

    public List<Author> getAllAuthors(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return authorDAO.getAll(session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
        return List.of();
    }

    public void deleteAuthor(String name){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Optional<Author> author = authorDAO.getByName(name, session);
            if (author.isEmpty()) {
                LOGGER.warning("Không tồn tại author với tên " + name);
                return;
            }
            authorDAO.deleteOne(author.get(), session);
        }catch (Exception e){
            HibernateUtil.printHibernateException(e);
        }
    }
}
