package org.sid.cloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableCircuitBreaker

public class CloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudGatewayApplication.class, args);
    }

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder)
    {
        return builder.routes()
                .route(r->r.path("/avions/**").uri("lb://AVION-SERVICE").id("r1"))
                .route(r->r.path("/passagers/**").uri("lb://PASSAGER-SERVICE").id("r2"))
                .build();
    }





}

@RestController
class FallBackRestController{
    @GetMapping("/restCountriesFallback")
    public Map<String,String> restCountriesFallback(){
        Map<String,String> map=new HashMap<>();
        map.put("message","Default Rest Countries Fallback service");
        map.put("countries","Algeria, Morocco");
        return map;
    } @
            GetMapping("/muslimsalatFallback")
    public Map<String,String> muslimsalatback(){
        Map<String,String> map=new HashMap<>();
        map.put("message","Default Muslim Fallback service");
        map.put("Fajr","07:00");
        map.put("DOHR","14:00");
        return map;
    }
}



