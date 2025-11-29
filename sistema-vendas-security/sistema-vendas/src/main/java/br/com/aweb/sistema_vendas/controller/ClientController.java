package br.com.aweb.sistema_vendas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_vendas.model.Client;
import br.com.aweb.sistema_vendas.service.ClientService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping
    public ModelAndView findAll() {
        return new ModelAndView("client/home", Map.of("clients", clientService.findAll()));
    }

    @GetMapping("/create")
    public ModelAndView createClient() {
        return new ModelAndView("client/form", Map.of("client", new Client()));
    }

    @PostMapping("/create")
    public String createClient(@Valid Client client, BindingResult result) {
        if (result.hasErrors()) {
            return "client/form";
        }
        clientService.createClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editClient(@PathVariable Long id) {
        var optionalClient = clientService.findById(id);
        if (optionalClient.isPresent()) {
            return new ModelAndView("client/form", Map.of("client", optionalClient.get()));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);

    }

    @PostMapping("/edit/{id}")
    public String postMethodName(@Valid Client client, BindingResult result) {
        if (result.hasErrors()) {
            return "client/form";
        }
        clientService.updateClient(client.getId(), client);
        return "redirect:/clients";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteClientGet(@PathVariable Long id) {
        return new ModelAndView("client/delete", Map.of("client", clientService.findById(id).get()));
    }

    @PostMapping("/delete/{id}")
    public String deleteClientPost(@PathVariable Long id) {
        clientService.deleteClient(id);
        return "redirect:/clients";
    }

}
