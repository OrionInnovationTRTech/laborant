package tr.com.orioninc.laborant;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tr.com.orioninc.laborant.controller.AdminController;
import tr.com.orioninc.laborant.service.AdminService;

@SpringBootApplication
@Log4j2
public class InternProjectApplication {

	public static void main(String[] args) throws Exception
	{
		log.info(" [MAIN] Starting application");
		SpringApplication.run(InternProjectApplication.class, args);


	}

}
