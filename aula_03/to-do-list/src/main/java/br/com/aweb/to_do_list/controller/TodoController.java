package br.com.aweb.to_do_list.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
// definição da classe como controller
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.aweb.to_do_list.model.Todo;
import br.com.aweb.to_do_list.repository.TodoRepository;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    TodoRepository todoRepository;

/* 
    /// nessa linha adicionamos a rota de comunicação (home) estanciada na url
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
        */
    @GetMapping
    public ModelAndView list(){
        /*ModelAndView modelAndView = new ModelAndView("list");
        modelAndView.addObject("todos", todoRepository.findAll());
        return modelAndView;
        */

        //forma menos facil de desenvolver 
        //return new ModelAndView("list",Map.of("todos",todoRepository.findAll()));

        return new ModelAndView(
            "list",
            Map.of("todos",todoRepository.findAll(Sort.by("deadline"))));
    }


    @GetMapping("/create")
    public ModelAndView create(){

        return new ModelAndView("form", Map.of("todo", new Todo()));
    }

    @PostMapping("/create")
    public String save (@Valid Todo todo, BindingResult result, RedirectAttributes attributes) {
        if(result.hasErrors())
         return "form";

        todoRepository.save(todo);
        return"redirect:/todo";
    }

      @GetMapping("/edit/{id}")
    public ModelAndView create(@PathVariable Long id){
        Optional <Todo> todo = todoRepository.findById(id);
        if(todo.isPresent())
            return new ModelAndView("form", Map.of("todo", todo.get()));
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public String save (@Valid Todo todo, BindingResult result, RedirectAttributes attributes) {
        if(result.hasErrors())
         return "form";

        todoRepository.save(todo);
        return"redirect:/todo";
    }
    


}
