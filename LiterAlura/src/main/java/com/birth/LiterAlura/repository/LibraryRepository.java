package com.birth.LiterAlura.repository;

import com.birth.LiterAlura.model.Library;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository


public interface LibraryRepository extends JpaRepository<Library, Long> {

}