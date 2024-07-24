package t1.springjwtt1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import t1.springjwtt1.security.jwt.JwtTokenProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtTokenProperties.class)
public class SpringJwtT1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringJwtT1Application.class, args);
    }

}
