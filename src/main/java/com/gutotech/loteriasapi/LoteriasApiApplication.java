package com.gutotech.loteriasapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LoteriasApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoteriasApiApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startupComplete() {
        System.out.println("\n=================================================");
        System.out.println("🎲 Servidor rodando em http://localhost:8090");
        System.out.println("📚 Documentação Swagger: http://localhost:8090/swagger-ui/doc");
        System.out.println("=================================================\n");
    }
}
