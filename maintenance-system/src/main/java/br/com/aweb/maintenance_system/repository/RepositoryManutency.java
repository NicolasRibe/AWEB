package br.com.aweb.maintenance_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.aweb.maintenance_system.model.Manutency;

@Repository
public interface RepositoryManutency  extends JpaRepository<Manutency, Long>{
    
    public List<Manutency> findByLocalContaining(String local );
}
