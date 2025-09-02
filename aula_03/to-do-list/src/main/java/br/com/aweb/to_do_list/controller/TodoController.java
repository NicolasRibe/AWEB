package br.com.aweb.to_do_list.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/todo")
public class TodoController {


    // nessa linha adicionamos a rota de comunicação (home) estanciada na url
    @GetMapping("/home")

    public ModelAndView home(){
        // nesse caso criamos o objeto que ira receber a model
        var modelAndView = new ModelAndView("home");
        modelAndView.addObject("professor", "Nicolas");
        var alunos = List.of(
            "Isaac Newton","Albert Einstein","Marie Curie");
        modelAndView.addObject("alunos", alunos);
        modelAndView.addObject("ehVerdade", true);
    

        return modelAndView;
    }

}
