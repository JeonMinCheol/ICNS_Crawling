package icns.crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Main Component
@SpringBootApplication
@EnableJpaAuditing
public class CrawlingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlingApplication.class, args);
	}

}
