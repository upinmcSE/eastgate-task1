use eastgate_software;

CREATE TABLE books (
   book_id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   nxb INT NOT NULL,
);


CREATE TABLE authors (
     author_id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     age INT
);

CREATE TABLE patrons (
     patron_id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     age INT
);

CREATE TABLE book_author (
     book_id INT,
     author_id INT,
     PRIMARY KEY (book_id, author_id),
     CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
     CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
);

CREATE TABLE patron_book (
     patron_id INT,
     book_id INT,
     status ENUM('ON', 'OFF'),
     PRIMARY KEY (patron_id, book_id),
     CONSTRAINT fk_patron FOREIGN KEY (patron_id) REFERENCES patrons(patron_id) ON DELETE CASCADE,
     CONSTRAINT fk_book2 FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);


ALTER TABLE books ADD COLUMN available_count INT DEFAULT 0;
ALTER TABLE books ADD COLUMN borrowed_count INT DEFAULT 0;


SELECT b.book_id, b.name, b.nxb
FROM books b
JOIN book_author ba ON b.book_id = ba.book_id
WHERE ba.author_id = ?;


SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE books;
TRUNCATE TABLE authors;
TRUNCATE TABLE patrons;
TRUNCATE TABLE book_author;
TRUNCATE TABLE patron_book;






