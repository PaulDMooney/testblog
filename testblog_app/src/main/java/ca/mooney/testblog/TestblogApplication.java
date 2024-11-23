package ca.mooney.testblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.SpringDataMongoDB;

@SpringBootApplication(exclude = {SpringDataMongoDB.class})
public class TestblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestblogApplication.class, args);
	}

}
