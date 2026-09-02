package com.example.demo.repository;
import com.example.demo.entity.Disputa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisputaRepository extends JpaRepository<Disputa, Long> {}
