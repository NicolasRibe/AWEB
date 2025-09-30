package br.com.compress.comunica_compress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.compress.comunica_compress.model.Compressor;

import java.util.Optional;



public interface RepositoryCompressor extends JpaRepository<Compressor,Integer> {

Optional<Compressor> findTopByIdOrderByDataHoraDesc(Integer idCompressor);
    
}