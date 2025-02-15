package com.gutotech.loteriasapi.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.ResultadoId;
import com.gutotech.loteriasapi.repository.ResultadoRepository;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository repository;

    // Busca todos os resultados de uma loteria e ordena em ordem decrescente pelo
    // concurso
    @Cacheable("resultados")
    public List<Resultado> findByLoteria(String loteria) {
        return repository.findById_Loteria(loteria) //
                .stream() //
                .sorted(Comparator.comparing(Resultado::getConcurso).reversed()) // Ordenação manual
                .collect(Collectors.toList());
    }

    // Busca um resultado por loteria e número de concurso
    public Resultado findByLoteriaAndConcurso(String loteria, int concurso) {
        return repository.findById(new ResultadoId(loteria, concurso)).orElse(null);
    }

    // Busca o resultado mais recente (último concurso) de uma loteria
    public Resultado findLatest(String loteria) {
        List<Resultado> resultados = repository.findById_Loteria(loteria)
                .stream()
                .sorted(Comparator.comparing(Resultado::getConcurso).reversed())
                .collect(Collectors.toList());

        if (resultados.isEmpty()) {
            return new Resultado();
        }

        return resultados.get(0);
    }

    // Salvar um único resultado
    public void save(Resultado resultado) {
        repository.save(resultado);
    }

    // Salvar vários resultados de uma só vez
    public void saveAll(List<Resultado> resultados) {
        repository.saveAll(resultados);
    }
}