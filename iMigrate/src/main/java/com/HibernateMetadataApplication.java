package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.entities.HibernateMetadataApplicationTests;

@SpringBootApplication
public class HibernateMetadataApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HibernateMetadataApplication.class, args);
		HibernateMetadataApplicationTests obj = new HibernateMetadataApplicationTests();
		//obj.contextLoads();
	}
}
