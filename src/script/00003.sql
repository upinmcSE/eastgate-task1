-- Add version column for optimistic locking
ALTER TABLE books ADD COLUMN version BIGINT DEFAULT 0;

-- Add composite index for patron-book checking
CREATE INDEX idx_patron_book_composite ON patron_book(patron_id, book_id, status);

-- Add partial index cho available books
CREATE INDEX idx_books_available ON books(id) WHERE available_count > 0;

-- Add index for book name lookups
CREATE UNIQUE INDEX idx_books_name ON books(name);