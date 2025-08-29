package init.upinmcSE.model;

import java.util.List;

public class Book {
    private int id;
    private String name;
    private int nxb;
    private List<Author> authors;

    public Book(){}

    public Book(int id, String name, int nxb, List<Author> authors) {
        this.id = id;
        this.name = name;
        this.nxb = nxb;
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nxb='" + nxb + '\'' +
                '}';
    }
}
