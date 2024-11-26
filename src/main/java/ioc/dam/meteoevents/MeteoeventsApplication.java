package ioc.dam.meteoevents;

import ioc.dam.meteoevents.service.PwdEncMigracioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MeteoeventsApplication {

	public static void main(String[] args) {
		// Inicia l'aplicació Spring Boot i obté el context de l'aplicació
		ApplicationContext context = SpringApplication.run(MeteoeventsApplication.class, args);

		// Accedeix al PwdEncMigracioService des del context
		PwdEncMigracioService pwdEncMigracioService = context.getBean(PwdEncMigracioService.class);

		// Executa el procés de migració de contrasenyes
		try {
			pwdEncMigracioService.migratePasswordsToEncryptedFormat();
			System.out.println("Procés de migració de contrasenyes completat amb èxit.");
		} catch (Exception e) {
			System.err.println("Error durant el procés de migració de contrasenyes: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
