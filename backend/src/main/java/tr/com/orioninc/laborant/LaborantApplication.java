package tr.com.orioninc.laborant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LaborantApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LaborantApplication.class, args);
    }

}
