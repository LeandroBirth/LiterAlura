package com.birth.LiterAlura;

import com.birth.LiterAlura.controller.LibraryController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	private final LibraryController libraryController;

	public LiterAluraApplication(LibraryController libraryController) {
		this.libraryController = libraryController;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args); // Start the Spring application
	}

	@PostConstruct
	public void init() {
		libraryConfig(); // Initialize your configuration here
	}

	@Override
	public void run(String... args) throws Exception {
		libraryController.startMenu(); // Call the method to start the menu
	}

	private void libraryConfig() {
		// Your configuration logic here
	}
}
