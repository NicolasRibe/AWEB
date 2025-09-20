package br.com.aweb.gerenciamento_alunos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.gerenciamento_alunos.model.Aluno;
import br.com.aweb.gerenciamento_alunos.repository.AlunoRepository;

@Service
public class AlunosService {

    @Autowired
    AlunoRepository alunoRepository;

    public List<Aluno> buscarAlunos() {
        return alunoRepository.findAll();
    }

    // cadastrar/alterar aluno
    public Aluno addAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }
    // remover aluno

    public void deleteAluno(Long id) {
        if (!alunoRepository.existsById(id))
            throw new RuntimeException("Erro ao excluir aluno! ");
        alunoRepository.deleteById(id);
    }

    public Aluno buscarId(Long id){
        Optional<Aluno> aluno = alunoRepository.findById(id);
        if (aluno.isPresent()){
            return aluno.get();
        }
        throw new RuntimeException("Aluno não encontrado !");
    }

    public List<Aluno> buscaAlunoNome(String nome){
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }  
}
