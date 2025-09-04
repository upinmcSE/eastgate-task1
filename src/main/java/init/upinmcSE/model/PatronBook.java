package init.upinmcSE.model;

import javax.persistence.*;

@Entity
@Table(name = "patron_book")
public class PatronBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_id")
    private Patron patron;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public PatronBook() {
    }

    public PatronBook(Long id, Status status, Patron patron, Book book) {
        this.id = id;
        this.status = status;
        this.patron = patron;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Patron getPatron() {
        return patron;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "PatronBook{" +
                "id=" + id +
                ", status=" + status +
                ", patron=" + patron +
                ", book=" + book +
                '}';
    }
}
