package com.birth.LiterAlura.service;

import com.birth.LiterAlura.model.Book;
import com.birth.LiterAlura.model.Library;
import com.birth.LiterAlura.repository.LibraryRepository;
import com.birth.LiterAlura.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LibraryService {

    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void addBooksToLibrary(Long libraryId, List<Book> books) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library not found"));

        for (Book book : books) {
            if (!entityManager.contains(book)) {
                book = entityManager.merge(book);  // Attach detached book to the persistence context
            }
            library.addBook(book);  // Add book to the library
        }

        libraryRepository.save(library);  // Save the library with the updated book list
    }

    @Transactional
    public String createLibraryAndBook() {
        try {
            Library library = new Library();
            library = libraryRepository.save(library);

            Book book = new Book();
            book.setTitle("Título do Livro");
            book.setLibrary(library);

            bookRepository.save(book);

            return "Library and Book created successfully!";
        } catch (Exception e) {
            logger.error("Error while creating library and book: ", e);
            throw new RuntimeException("Error while creating library and book: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Book> listRegisteredBooks() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .collect(Collectors.toList()); // Convert Iterable to List
    }

    @Transactional(readOnly = true)
    public List<String> listRegisteredAuthors() {
        return bookRepository.findDistinctAuthors();
    }

    @Transactional(readOnly = true)
    public List<Book> listLivingAuthorsInYear(int year) {
        return bookRepository.findBooksByLivingAuthorsInYear(year);
    }

    @Transactional
    public void addBooksToLibrary(Library library) {
        // Validate the library
        if (library == null) {
            throw new IllegalArgumentException("Library cannot be null");
        }

        // Fetch or create a list of books to add (this can be from user input or an API, etc.)
        List<Book> booksToAdd = fetchBooksFromApi(); // Method to fetch or create the list of books

        // Iterate over the books and add them to the library
        for (Book book : booksToAdd) {
            // Check if the book is already in the library to avoid duplicates
            if (!library.getBooks().contains(book)) {
                // If the book is not managed, merge it into the current persistence context
                if (!entityManager.contains(book)) {
                    book = entityManager.merge(book);
                }
                library.addBook(book); // Use the Library's addBook method to add the book
            }
        }

        // Save the library with the new books
        libraryRepository.save(library);
    }

    // Example method to fetch or create a list of books
    private List<Book> fetchBooksFromApi() {
        // Implement your logic to retrieve books, e.g., from an external API or predefined list.
        // This is a placeholder for demonstration purposes.
        return List.of(new Book("Book Title 1"), new Book("Book Title 2")); // Example book creation
    }
}
