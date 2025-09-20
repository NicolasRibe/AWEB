package br.com.aweb.sistema_produto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_produto.model.Product;
import br.com.aweb.sistema_produto.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    // listar todos os produtos
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public List<Product> findByName(String name){
        return productRepository.findByNameLike(name);
    }

    // buscar produto por id
    public Product findProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return product.get();
        }
        throw new RuntimeException("Produto não encontrado!");
    }

    // cadastrar/alterar produto
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // remover produto
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id))
            throw new RuntimeException("Erro ao excluir produto!");

        productRepository.deleteById(id);
    }
}
