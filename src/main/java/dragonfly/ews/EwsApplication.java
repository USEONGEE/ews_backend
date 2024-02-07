package dragonfly.ews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EwsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwsApplication.class, args);
	}

}
