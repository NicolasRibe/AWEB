package br.com.aweb.sistema_vendas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_vendas.model.Product;
import br.com.aweb.sistema_vendas.service.ProductService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    public ModelAndView findAll() {
        return new ModelAndView("product/home", Map.of("products", productService.findAll()));
    }

    @GetMapping("/create")
    public ModelAndView create() {
        return new ModelAndView("product/form", Map.of("product", new Product()));
    }

    @PostMapping("/create")
    public String create(@Valid Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "product/form";
        }
        productService.create(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editGet(@PathVariable Long id) {
        return new ModelAndView("product/form", Map.of("product", productService.findById(id)));
    }

    @PostMapping("/edit/{id}")
    public String editPost(@Valid Product product) {
        productService.create(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteGet(@PathVariable Long id) {
        return new ModelAndView("product/delete", Map.of("product", productService.findById(id)));
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/products";
    }

}
