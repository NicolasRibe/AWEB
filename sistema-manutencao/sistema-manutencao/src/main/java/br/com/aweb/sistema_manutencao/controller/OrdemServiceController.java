package br.com.aweb.sistema_manutencao.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_manutencao.service.OrdemServicoService;

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

    }


    

    
    
}
