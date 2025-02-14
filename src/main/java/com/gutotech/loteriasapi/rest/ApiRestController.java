package com.gutotech.loteriasapi.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gutotech.loteriasapi.model.Loteria;
import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.exception.ResourceNotFoundException;
import com.gutotech.loteriasapi.service.ResultadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Loterias", description = "Endpoints para consultar resultados de loterias")
public class ApiRestController {

    private final List<String> lotteries = Loteria.asList();
    
        private final String invalidLotteryMessageFormat = "'%s' não é o id de nenhuma das loterias suportadas. Loterias suportadas: "
                + lotteries;
    
        @Autowired
        private ResultadoService resultadoService;
    
        @GetMapping
        @Operation(summary = "Retorna todas as loterias disponíveis para pesquisa.")
        public ResponseEntity<List<String>> getLotteries() {
            return ResponseEntity.ok(Loteria.asList());
        }
    
        @GetMapping("{loteria}")
        @Operation(summary = "Retorna todos os resultados já realizados da loteria especificada.")
        public ResponseEntity<List<Resultado>> getResultsByLottery(
                @Parameter(description = "Nome da loteria", example = "megasena", required = true) @PathVariable("loteria") String loteria) {
        if (!lotteries.contains(loteria)) {
            throw new ResourceNotFoundException(String.format(invalidLotteryMessageFormat, loteria));
        }

        return ResponseEntity.ok(resultadoService.findByLoteria(loteria));
    }

    @GetMapping("{loteria}/{concurso}")
    @Operation(summary = "Retorna o resultado da loteria e concurso especificado.")
    public ResponseEntity<Resultado> getResultById(
            @Parameter(description = "Nome da loteria", example = "megasena", required = true) @PathVariable("loteria") String loteria,
            @Parameter(description = "Número do concurso", example = "2500") @PathVariable("concurso") Integer concurso) {

        if (!lotteries.contains(loteria)) {
            throw new ResourceNotFoundException(String.format(invalidLotteryMessageFormat, loteria));
        }
        return ResponseEntity.ok(resultadoService.findByLoteriaAndConcurso(loteria, concurso));
    }

    @GetMapping("{loteria}/latest")
    @Operation(summary = "Retorna o resultado mais recente da loteria especificada.")
    public ResponseEntity<Resultado> getLatestResult(
            @Parameter(description = "Nome da loteria", example = "megasena", required = true) @PathVariable("loteria") String loteria) {

        if (!lotteries.contains(loteria)) {
            throw new ResourceNotFoundException(String.format(invalidLotteryMessageFormat, loteria));
        }
        return ResponseEntity.ok(resultadoService.findLatest(loteria));
    }
}