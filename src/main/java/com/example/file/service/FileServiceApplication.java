package com.example.file.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class FileServiceApplication {

	public static void main(String[] args) {
		init();
		SpringApplication.run(FileServiceApplication.class, args);
	}

	private static void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Locale.setDefault(Locale.US);
	}
}