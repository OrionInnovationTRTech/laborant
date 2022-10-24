package tr.com.orioninc.laborant;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class InternProjectApplication {

	public static void main(String[] args) throws Exception
	{
		log.info("Application started");
		SpringApplication.run(InternProjectApplication.class, args);


	}

}
