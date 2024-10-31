package com.birth.LiterAlura.repository;

import com.birth.LiterAlura.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

    @Query("SELECT DISTINCT author.name FROM Book b JOIN b.authors author")
    List<String> findDistinctAuthors();

    @Query("SELECT b FROM Book b JOIN b.authors author WHERE author.isAlive = true AND author.birthYear <= :year AND (author.deathYear IS NULL OR author.deathYear >= :year)")
    List<Book> findBooksByLivingAuthorsInYear(@Param("year") int year);

    @Query("SELECT b FROM Book b WHERE :language MEMBER OF b.languages")
    List<Book> findBooksByLanguage(@Param("language") String language);

    @Query("SELECT b FROM Book b")
    List<Book> findAllBooks();
}
