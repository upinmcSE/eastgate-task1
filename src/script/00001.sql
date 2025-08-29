use eastgate-software;

CREATE TABLE books (
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   nxb INT NOT NULL
);


CREATE TABLE authors (
     id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     age INT,
)

CREATE TABLE patrons (
     id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     age INT,
)

CREATE TABLE book_author (
     book_id INT,
     author_id INT,
     PRIMARY KEY (book_id, author_id),
     CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
     CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
)

CREATE TABLE patron_book (
     patron_id INT,
     book_id INT,
     status ENUM('ON', 'OFF')
	 PRIMARY KEY (patron_id, book_id),
     CONSTRAINT fk_patron FOREIGN KEY (patron_id) REFERENCES patrons(id),
     CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id),
)