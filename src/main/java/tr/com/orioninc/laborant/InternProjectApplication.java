package tr.com.orioninc.laborant;

import io.swagger.annotations.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class InternProjectApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(InternProjectApplication.class, args);
	}

}
