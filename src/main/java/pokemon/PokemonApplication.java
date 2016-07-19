package pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class PokemonApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		new PokemonApplication()
				.configure(new SpringApplicationBuilder(PokemonApplication.class))
				.run(args);
	}
}
