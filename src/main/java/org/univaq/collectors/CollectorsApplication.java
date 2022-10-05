package org.univaq.collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollectorsApplication implements CommandLineRunner {
	@Autowired
	private DatabaseGenerator databaseGenerator;
	@Override
	public void run(String... args) throws Exception {
		databaseGenerator.generateDatabase();

	}


	public static void main(String[] args) {
		SpringApplication.run(CollectorsApplication.class, args);
	}

}
