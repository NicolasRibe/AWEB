package br.com.aweb.sistema_vendas.service;

import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.repository.RepositoryProdutos;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutosService {

    // injeção de dependencia
    // a independencia serve para que a classe service possa usar os metodos do
    // repository
    @Autowired
    RepositoryProdutos repositoryProdutos;

    // Lógica de negócio relacionada aos produtos
    // metodo para salvar produto

    // esta anotação indica que o método deve ser executado dentro de uma transação
    //toda vez que for chamado esse metodo, uma transação será iniciada
    //se nao der ele volta ao estado anterior
    @Transactional
    //O metodo retorna o produto para confirmar que foi salvo
    //se fosse void não retornaria nada
    public Produto salvarProduto(Produto produto) {
        // Lógica para salvar o produto
        //

        if(repositoryProdutos.existsByNome(produto.getNome())) {
            throw new IllegalArgumentException("Já existe um produto com esse nome: " + produto.getNome());
        }
        return repositoryProdutos.save(produto);

    }

    //metodo para buscar produto por id
    public Produto buscarProdutoPorId(Long id) {
        // Lógica para buscar o produto pelo ID
        return repositoryProdutos.findById(id).orElse(null);
    }

    public void deletarProduto(Long id) {
        // Lógica para deletar o produto pelo ID
        repositoryProdutos.deleteById(id);
    }
    public Produto atualizarProduto(Produto produto) {
        // Lógica para atualizar o produto
        return repositoryProdutos.save(produto);
    }

    public List<Produto> listarTodosProdutos() {
        // Lógica para listar todos os produtos
        // o repositoryProdutos.findAll() retorna uma lista de produtos
        return repositoryProdutos.findAll();
    }
}