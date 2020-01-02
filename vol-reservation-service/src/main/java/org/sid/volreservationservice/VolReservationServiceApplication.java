package org.sid.volreservationservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
class Vol{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String aeroportDepart;
    private String aeroportDestination;

    @Transient
    @OneToMany(mappedBy = "vol")
    private Collection<Reservation> reservations;
    @Transient
    private Avion avion;
    private long avionID;
}

/*@Projection(name = "fullAvion", types = Avion.class)
interface AvionProjection extends Projection{
    public Long getId();
    public String getDesignation();
    public String getNomCompanie();
    public String getNombrePlace();
}*/

@RepositoryRestResource
interface VolRepository extends JpaRepository<Vol,Long> {}

@Entity @Data @NoArgsConstructor @AllArgsConstructor
class Reservation{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private int numeroPlace;
    private double price;

    @Transient
    private Passager passager;
    private long passagerID;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Vol vol;
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation,Long>{
    List<Reservation> findByVolId(Long volID);
}

@Data
class Passager{
    private Long id;
    private String name;
    private String numeroPasseport;
}

@Data
class Avion{
    private Long id;
    private String designation;
    private String nomCompanie;
}

@FeignClient(name="avion-service")
interface AvionServiceClient{
    @GetMapping("/avions/{id}?projection=fullAvion")
    Avion findAvionById(@PathVariable("id") Long id);
}
@FeignClient(name="passager-service")
interface PassagerServiceClient{
    @GetMapping("/passagers/{id}?projection=fullPassager")
    Passager findPassagerById(@PathVariable("id") Long id);
    @GetMapping("/passagers?projection=fullPassager")
    PagedModel<Passager> findAll();
}

@RestController
class VolRestController{
    @Autowired private VolRepository volRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private AvionServiceClient avionServiceClient;
    @Autowired private PassagerServiceClient passagerServiceClient;
    @GetMapping("/vol/full/{id}")
    Vol getVol(@PathVariable(name="id") Long id){
        Vol vol=volRepository.findById(id).get();
        vol.setAvion(avionServiceClient.findAvionById(vol.getAvionID()));
        vol.setReservations(reservationRepository.findByVolId(id));
        vol.getReservations().forEach(pi->{
            pi.setPassager(passagerServiceClient.findPassagerById(pi.getPassagerID()));
        });
        return vol; }
}

@SpringBootApplication
@EnableFeignClients
public class VolReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolReservationServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(VolRepository volRepository, ReservationRepository reservationRepository, PassagerServiceClient passagerServiceClient, AvionServiceClient avionServiceClient){
        return args -> {
            Vol vol=new Vol();
            vol.setDate(new Date());
            vol.setAeroportDepart("MED5");
            vol.setAeroportDestination("MADRID");
            Avion avion=avionServiceClient.findAvionById(1L);
            vol.setAvionID(avion.getId());
            volRepository.save(vol);
            passagerServiceClient.findAll().getContent().forEach(p->{
                reservationRepository.save(new Reservation(null,new Date(),12,2000.0,null,p.getId(),vol));
            });
        };
    }

}
