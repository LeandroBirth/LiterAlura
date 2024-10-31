package com.birth.LiterAlura.controller;

import com.birth.LiterAlura.model.Book;
import com.birth.LiterAlura.model.Library;
import com.birth.LiterAlura.service.LibraryService;
import com.birth.LiterAlura.service.ApiFetch;
import com.birth.LiterAlura.repository.LibraryRepository; // Ensure to import this
import com.birth.LiterAlura.repository.BookRepository; // Ensure to import this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    private final LibraryService libraryService;
    private final ApiFetch apiFetch;
    private final LibraryRepository libraryRepository; // Injected LibraryRepository
    private final BookRepository bookRepository; // Injected BookRepository

    private final String baseURL = "https://gutendex.com/books";
    private final String URL_search = "?search=";
    private final String URL_language = "?languages=";

    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public LibraryController(LibraryService libraryService, ApiFetch apiFetch, LibraryRepository libraryRepository, BookRepository bookRepository) {
        this.libraryService = libraryService;
        this.apiFetch = apiFetch;
        this.libraryRepository = libraryRepository; // Injected
        this.bookRepository = bookRepository; // Injected
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBooksToLibrary(@RequestBody Library library) {
        try {
            // Validate input
            if (library == null || library.getBooks().isEmpty()) {
                logger.warn("Library or book list is empty.");
                return ResponseEntity.badRequest().body("Library or book list cannot be empty.");
            }

            // Save the library
            library = libraryRepository.save(library);
            for (Book book : library.getBooks()) {
                book.setLibrary(library); // Link each book to the library
                bookRepository.save(book); // Save each book
            }
            logger.info("Books added to library successfully: " + library);
            return ResponseEntity.ok("Books added to library successfully!");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument: ", e);
            return ResponseEntity.badRequest().body("Invalid input data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding books to library: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding books.");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createLibraryAndBook() {
        try {
            String result = libraryService.createLibraryAndBook();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error creating library and book: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the library and book.");
        }
    }

    private void menu() {
        String apiURL;
        Library library = new Library();

        System.out.println("""
                .........===
                Escolha o número de sua opção:
                1- Buscar livro pelo título
                2- Listar Livros registrados
                3- Listar autores registrados
                4- Listar autores vivos em um determinado ano
                5- Listar livros em um determinado idioma
                0- Sair
                .........===
                """);

        int chosenOption = scanner.nextInt();
        scanner.nextLine();

        switch (chosenOption) {
            case 1 -> {
                // Implementação da busca de livro pelo título
                System.out.println("Digite o nome do livro:");
                String nomeLivro = scanner.nextLine();
                apiURL = baseURL + URL_search + nomeLivro;
                apiFetch.fetchData(apiURL, library);

                if (library.getBooks().isEmpty()) {
                    System.out.println("Nenhum livro encontrado.");
                } else {
                    for (Book book : library.getBooks()) {
                        System.out.println("Título: " + book.getTitle());
                        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                            book.getAuthors().forEach(author -> System.out.println("Autor: " + author.getName()));
                        } else {
                            System.out.println("Autor: Não disponível");
                        }

                        System.out.println("Linguagem: " + book.getLanguages());
                        System.out.println("Assunto: " + book.getSubjects());

                        // Link to the library and save each book individually
                        book.setLibrary(library); // Link to the library
                        libraryService.addBooksToLibrary(library); // Use LibraryService to save
                    }
                }
            }
            case 2 -> {
                System.out.println("Lista de livros registrados:");
                List<Book> books = libraryService.listRegisteredBooks();
                if (books.isEmpty()) {
                    System.out.println("Nenhum livro registrado.");
                } else {
                    for (Book book : books) {
                        System.out.println("Título: " + book.getTitle());
                    }
                }
            }
            case 3 -> {
                System.out.println("Lista de autores registrados:");
                List<String> authors = libraryService.listRegisteredAuthors();
                if (authors.isEmpty()) {
                    System.out.println("Nenhum autor registrado.");
                } else {
                    authors.forEach(author -> System.out.println("Autor: " + author));
                }
            }
            case 4 -> {
                System.out.println("Digite o ano:");
                int ano = scanner.nextInt();
                scanner.nextLine();
                List<Book> books = libraryService.listLivingAuthorsInYear(ano);
                if (books.isEmpty()) {
                    System.out.println("Nenhum autor vivo encontrado no ano " + ano + ".");
                } else {
                    for (Book book : books) {
                        System.out.println("Título: " + book.getTitle());
                    }
                }
            }
            case 5 -> {
                System.out.println("Digite o idioma (código de idioma):");
                String idioma = scanner.nextLine();
                List<Book> books = libraryService.listBooksByLanguage(idioma);
                if (books.isEmpty()) {
                    System.out.println("Nenhum livro encontrado no idioma " + idioma + ".");
                } else {
                    for (Book book : books) {
                        System.out.println("Título: " + book.getTitle());
                    }
                }
            }
            case 0 -> System.out.println("Saindo! Obrigado.");
            default -> System.out.println("Opção inválida. Tente novamente.");
        }
    }

    public void startMenu() {
        menu();
    }
}
