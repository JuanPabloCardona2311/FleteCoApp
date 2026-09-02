package com.example.demo.repository;
import com.example.demo.entity.Despachador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DespachadorRepository extends JpaRepository<Despachador, Long> {
    Optional<Despachador> findByUsuarioId(Long usuarioId);
}
