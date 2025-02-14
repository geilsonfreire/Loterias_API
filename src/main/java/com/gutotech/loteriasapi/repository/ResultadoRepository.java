package com.gutotech.loteriasapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.ResultadoId;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, ResultadoId> {

    // Busca todos os resultados por loteria
    List<Resultado> findById_Loteria(String loteria);

    // Busca o resultado mais recente para a loteria especificada
    @Query("SELECT r FROM Resultado r WHERE r.id.loteria = ?1 ORDER BY r.id.concurso DESC")
    Optional<Resultado> findTopById_Loteria(String loteria);
}
