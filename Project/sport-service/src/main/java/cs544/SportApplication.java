package cs544;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportApplication{

    public static void main(String[] args) {
        SpringApplication.run(SportApplication.class, args);
    }

}