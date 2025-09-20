package br.com.aweb.sistema_vendas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aweb.sistema_vendas.model.Produto;

public interface RepositoryProdutos extends JpaRepository<Produto, Long> {
    

    public List<Produto> findByNomeContaining(String nome );

    public boolean existsByNome(String nome);
}
