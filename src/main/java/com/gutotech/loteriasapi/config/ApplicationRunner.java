//Caminho para o arquivo que será criado.
package com.gutotech.loteriasapi.config;

// Importação das classes necessárias.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// Importação da classe LoteriasUpdate.
import com.gutotech.loteriasapi.consumer.LoteriasUpdate;

@Configuration
@EnableCaching
public class ApplicationRunner implements CommandLineRunner {

    @Autowired
    private LoteriasUpdate loteriasUpdate;

    @Override
    public void run(String... args) {
        loteriasUpdate.checkForUpdates();
    }

}
