package org.sid.avionservice;

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
class Avion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String designation;
    private String nomCompanie;
    private int nombrePlace ;
}

@Projection(name = "fullAvion", types = Avion.class)
interface AvionProjection extends Projection{
    public Long getId();
    public String getDesignation();
    public String getNomCompanie();
    public String getNombrePlace();
}

@RepositoryRestResource
interface AvionRepository extends JpaRepository<Avion,Long> {}
@SpringBootApplication
public class AvionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvionServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(AvionRepository avionRepository ){
        return args -> {
            avionRepository.save(new Avion(null,"bwing-2022","FlyEmarat",60));
            avionRepository.save(new Avion(null,"bwing-27782","RAM",60));
            avionRepository.save(new Avion(null,"bwing-2022","RAM",60));
            avionRepository.findAll().forEach(System.out::println);
        };
    }
}
