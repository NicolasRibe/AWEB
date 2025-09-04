package br.com.aweb.gerenciamento_alunos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.aweb.gerenciamento_alunos.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    public List<Aluno> findByNomeContainingIgnoreCase(String nome);

}
