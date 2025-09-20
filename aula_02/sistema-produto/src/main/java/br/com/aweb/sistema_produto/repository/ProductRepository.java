package br.com.aweb.sistema_produto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.aweb.sistema_produto.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  public List <Product> findByNameLike(String name);

}
