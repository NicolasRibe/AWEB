package br.com.aweb.sistema_vendas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aweb.sistema_vendas.model.Cliente;


public interface RepositoryCliente extends JpaRepository<Cliente, Long> {

    public List<Cliente> findByNomeContaining(String nome );
}
