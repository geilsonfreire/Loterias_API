package com.gutotech.loteriasapi.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.gutotech.loteriasapi.model.Loteria;
import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.service.ResultadoService;

@Component
public class LoteriasUpdate {

    @Autowired
    private LoteriaUpdateTask loteriaUpdateTask;

    @Autowired
    private CacheManager cacheManager;

    public void checkForUpdates() {
        System.out.println("\n🔄 Iniciando verificação de atualizações das loterias...\n");

        for (Loteria loteria : Loteria.values()) {
            try {
                if (loteria != null && loteria.getNome() != null) {
                    loteriaUpdateTask.checkForUpdates(loteria.getNome());
                }
            } catch (Exception e) {
                System.out.println("❌ Erro " + loteria + ": " + e.getMessage());
            }
        }

        Optional.ofNullable(cacheManager.getCache("resultados"))
                .ifPresent(cache -> cache.clear());

        System.out.println("\n✅ Verificação de atualizações concluída!\n");
    }

    @Component
    @EnableAsync
    class LoteriaUpdateTask {

        @Autowired
        private Consumer consumer;

        @Autowired
        private ResultadoService resultadoService;

        @Async
        public void checkForUpdates(String loteria) throws Exception {
            if (loteria == null) {
                return;
            }

            System.out.println("\n🔍 Verificando " + loteria + "...");

            Resultado latestResultado = consumer.getResultado(loteria, null);
            if (latestResultado == null) {
                return;
            }

            System.out.println("📥 Último concurso disponível: " + loteria + " #" + latestResultado.getConcurso());

            Resultado myLatestResultado = resultadoService.findLatest(loteria);
            if (myLatestResultado != null) {
                System.out.println("💾 Último concurso salvo: " + loteria + " #" + myLatestResultado.getConcurso());
            }

            if (myLatestResultado != null && Objects.equals(myLatestResultado.getConcurso(), latestResultado.getConcurso())) {
                System.out.println("✅ " + loteria + ": Dados já atualizados");
                myLatestResultado.setData(latestResultado.getData());
                myLatestResultado.setLocal(latestResultado.getLocal());

                myLatestResultado.setPremiacoes(latestResultado.getPremiacoes());
                myLatestResultado.setEstadosPremiados(latestResultado.getEstadosPremiados());
                myLatestResultado.setAcumulou(latestResultado.isAcumulou());
                myLatestResultado.setDataProximoConcurso(latestResultado.getDataProximoConcurso());

                resultadoService.save(myLatestResultado);
            } else if (myLatestResultado == null || myLatestResultado.getConcurso() < latestResultado.getConcurso()) {
                int inicio = (myLatestResultado == null ? 1 : myLatestResultado.getConcurso() + 1);
                System.out.println("🔄 " + loteria + ": Atualizando concursos " + inicio + " até " + latestResultado.getConcurso());

                Map<String, Integer> tentativasMap = new HashMap<>();

                for (int concurso = inicio; concurso <= latestResultado.getConcurso(); concurso++) {
                    try {
                        System.out.println("📝 " + loteria + ": Salvando concurso #" + concurso);
                        Resultado resultado = consumer.getResultado(loteria, String.valueOf(concurso));
                        if (resultado != null) {
                            resultadoService.save(resultado);
                            System.out.println("✅ " + loteria + ": Concurso #" + concurso + " salvo com sucesso");
                        }
                    } catch (Exception e) {
                        int total = tentativasMap.getOrDefault(loteria + "-" + concurso, 0);

                        if (total < 20) {
                            tentativasMap.put(loteria + "-" + concurso, ++total);
                            --concurso;

                            System.out.printf("ERRO: (%s, %d) - %s %s \n", loteria, concurso, e.getClass(),
                                    e.getMessage());
                        } else {
                            System.out.printf("PARANDO DE BUSCAR (%s, %d)\n", loteria, concurso);
                        }
                    }
                }

                System.out.println("✨ " + loteria + ": Atualização finalizada");
                System.out.println("✅ " + loteria + ": Atualização concluída!");
            }
        }
    }

}
