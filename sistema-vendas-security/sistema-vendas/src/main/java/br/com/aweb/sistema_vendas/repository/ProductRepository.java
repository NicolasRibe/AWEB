package br.com.aweb.sistema_vendas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aweb.sistema_vendas.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
