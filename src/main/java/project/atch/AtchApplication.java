package project.atch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import project.atch.global.oidc.dto.OauthProperties;

@EnableJpaAuditing
@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(OauthProperties.class)
public class AtchApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtchApplication.class, args);
	}

}
