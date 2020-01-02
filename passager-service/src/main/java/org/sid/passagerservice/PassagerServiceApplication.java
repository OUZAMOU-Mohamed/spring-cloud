package org.sid.passagerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
class Passager{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroPasseport;
    private String name;
    private String email;
}

@Projection(name = "fullPassager", types = Passager.class)
interface PassagerProjection extends Projection{
    public Long getId();
    public String getNumeroPasseport();
    public String getName();
    public String getEmail();
}

@RepositoryRestResource
interface  PassagerRepository extends JpaRepository<Passager,Long> {}
@SpringBootApplication
public class PassagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassagerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(PassagerRepository passagerRepository ){
        return args -> {
            passagerRepository.save(new Passager(null,"123445","med","med@gmail.com"));
            passagerRepository.save(new Passager(null,"125434","ali","ali@gmail.com"));
            passagerRepository.save(new Passager(null,"123445","sanaa","sanaa@gmail.com"));
            passagerRepository.findAll().forEach(System.out::println);
        };
    }
}
