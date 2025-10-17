package br.com.aweb.sistema_vendas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.service.ProdutosService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// Anotação que indica que essa classe é um controlador Spring MVC
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    // Injeção de dependência do serviço de produtos
    @Autowired
    ProdutosService produtosService;
    // Métodos para lidar com requisições HTTP relacionadas a produtos

    @GetMapping("/create")
    // método para exibir o formulário de criação de produto
    public ModelAndView create() {
        // retorna a view form.html
        // o Map.of cria um mapa imutável com a chave "produto" e um novo objeto Produto
        // vazio
        // esse objeto será usado para preencher os campos do formulário
        return new ModelAndView("produto/form", Map.of("produto", new Produto()));
    }

    @PostMapping("/create")
    // método para salvar o produto no banco de dados
    public String save(@Valid Produto produto, BindingResult result) {
        // verifica se há erros de validação
        if (result.hasErrors()) {
            // se houver erros, retorna para a página do formulário com os erros
            return "produto/form";
        }
        // chama o serviço para salvar o produto
        produtosService.salvarProduto(produto);
        // redireciona para a página inicial (root)
        return "redirect:/produtos";
    }
    @GetMapping
    public ModelAndView list() {
        // lista todos os produtos e passa para a view list.html
        return new ModelAndView("produto/home", Map.of("produtos", produtosService.listarTodosProdutos()));
    }
//método para deletar produto
//
    @GetMapping("/delete/{id}")
    public String delete(Long id) {
        // chama o serviço para deletar o produto pelo ID
        produtosService.deletarProduto(id);
        // redireciona para a página de listagem de produtos
        return "redirect:/produtos";
    }
    

}
