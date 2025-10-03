package br.com.aweb.sistema_manutencao.service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;


import org.springframework.web.bind.annotation.PathVariable;



import br.com.aweb.sistema_manutencao.model.OrdemServico;
import br.com.aweb.sistema_manutencao.repository.OrdemServicoRepository;


// Toda a Logica de negocio esta dentro desta classe;
@Service
public class OrdemServicoService {

    // Injeção de dependencia para busca e ação no banco de dados

    @Autowired
    OrdemServicoRepository ordemServicoRepository;

    // Metodo para Listar Ordens
    // Aqui Chamamos a classe list e passamos o
    // parametro OrdensServiço para serem listados
    public List<OrdemServico> listarOrdens() {
        // para isso retornamos o objeto da classe repository que busca todos os objetos
        // criados;
        return ordemServicoRepository.findAll();
    }
    // Criando um metodo de busca por ID
    // Esse metodo busca com parametro inserido(id)
    // Apos isso chamamos a classe Pai que faz a tratativa com parametro ordem de
    // serviço
    // para usarmos o metodo isPresent e gerarmos uma exeção caso nao existir o id
    public OrdemServico buscarOrdemId(Long id) {
        Optional<OrdemServico> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isPresent()) {
            return ordemServico.get();
        }
        throw new RuntimeException("ID da Ordem não encontrada !!");
    }

    public OrdemServico save(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }

    public void delete(@PathVariable Long id) {
       if (!ordemServicoRepository.existsById(id))
            throw new RuntimeException("Erro ao excluir Ordem!");
        ordemServicoRepository.deleteById(id);
    }
    
    public void finalizaOrdemServico(Long id){

        var ordemServico = ordemServicoRepository.findById(id);

        if(!ordemServico.isPresent())
          throw new RuntimeException("Erro ao finalizar Ordem!");
        
        ordemServico.get().setFinalizacaoOrdem(LocalDateTime.now());
        ordemServicoRepository.save(ordemServico.get());

    }

}
