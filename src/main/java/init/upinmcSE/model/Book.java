package init.upinmcSE.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int nxb;
    private int availableCount;
    private int borrowedCount;

    @Version
    private long version;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author",
                joinColumns = {@JoinColumn(name = "book_id")},
                inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatronBook> patronBooks;

    public Book(){}

    public Book(String name, int nxb, int availableCount, int borrowedCount, long version, Set<Author> authors) {
        this.name = name;
        this.nxb = nxb;
        this.availableCount = availableCount;
        this.borrowedCount = borrowedCount;
        this.version = version;
        this.authors = authors;
    }

    public Book(int id, String name, int nxb, int availableCount, int borrowedCount, long version, Set<Author> authors) {
        this.id = id;
        this.name = name;
        this.nxb = nxb;
        this.availableCount = availableCount;
        this.borrowedCount = borrowedCount;
        this.version = version;
        this.authors = authors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNxb() {
        return nxb;
    }

    public void setNxb(int nxb) {
        this.nxb = nxb;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public int getAvailableCount() {
        return this.availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public int getBorrowedCount() {
        return this.borrowedCount;
    }

    public void setBorrowedCount(int borrowedCount) {
        this.borrowedCount = borrowedCount;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nxb=" + nxb +
                ", availableCount=" + availableCount +
                ", borrowedCount=" + borrowedCount +
                ", authors=" + authors +
                '}';
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
