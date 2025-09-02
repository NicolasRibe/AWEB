package br.com.aweb.crud_no_db.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.aweb.crud_no_db.dto.ProdutoDTO;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    
    private Map<Long, ProdutoDTO> products = new HashMap<>();
    private Long nextId = 1L;

    // listar todos produtos
    @GetMapping
    public List<ProdutoDTO> allProducts(){
        return new ArrayList<>(products.values());
    }

    // buscar produto por ID
    @GetMapping("/{id}")
    public ProdutoDTO GetProductById(@PathVariable Long id){
        return products.get(id);
    }

    // criar produto
    @PostMapping
    public ProdutoDTO CreateProduct(@RequestBody ProdutoDTO product){
        product.setId(nextId++);
        products.put(product.getId(), product);
        return product;
    }

    // remover produto
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id){
        if(products.remove(id) != null)
            return "Produto removido!";
        return "Produto não encontrado!";
    }
}
