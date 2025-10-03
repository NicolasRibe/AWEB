package br.com.aweb.sistema_manutencao.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_manutencao.model.OrdemServico;
import br.com.aweb.sistema_manutencao.service.OrdemServicoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ordemServico")
public class OrdemServiceController {

    @Autowired
    OrdemServicoService ordemServicoService;

    @GetMapping
    public ModelAndView listarOrdem() {
        return new ModelAndView ("home", Map.of("todasOrdens", ordemServicoService.listarOrdens()));
    }

    @GetMapping("/home/{id}")
    public ModelAndView buscarPorID(@PathVariable Long id){
       
        var busca= ordemServicoService.buscarOrdemId(id);

        return new ModelAndView ("home", Map.of("buscaId", busca));
    }


    @GetMapping("/new")
    public ModelAndView criarOrdem(){
        return new ModelAndView("form", Map.of("ordem", new OrdemServico()));
    }

    @PostMapping("/new")
    public String criarOrdem(@Valid OrdemServico ordemServico, BindingResult result){
        if(result.hasErrors())
            return "form";
        ordemServicoService.save(ordemServico);
        return "redirect:/ordemServico";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editOrdem(@PathVariable Long id) {
        return new ModelAndView("form", Map.of("ordem", ordemServicoService.buscarOrdemId(id)));
    }


    @PostMapping("/edit/{id}")
    public String edit(@Valid OrdemServico ordemServico, BindingResult result){
        if(result.hasErrors())
            return "form";
        ordemServicoService.save(ordemServico);
        return"redirect:/ordemServico";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        ordemServicoService.delete(id);
        return"redirect:/ordemServico";
    }

    @PostMapping("/finalizar/{id}")
    public String finalizar(@PathVariable Long id){
        ordemServicoService.finalizaOrdemServico(id);
        return "redirect:/ordemServico";
    }

}


    

    
    

