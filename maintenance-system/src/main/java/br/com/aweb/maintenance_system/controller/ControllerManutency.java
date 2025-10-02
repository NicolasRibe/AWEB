package br.com.aweb.maintenance_system.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.aweb.maintenance_system.model.Manutency;
import br.com.aweb.maintenance_system.service.ServiceManutency;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;




@Controller
@RequestMapping("/ordensServ")
public class ControllerManutency {

  @Autowired
  ServiceManutency serviceManutency;

  @GetMapping
  public String listaOrdedens(Model model) {
    model.addAttribute("ordens", serviceManutency.listAll());
    return "list";
  }

  @GetMapping("/newOrdem")
  public String formNewOrdem(Model model) {
    model.addAttribute("ordem", new Manutency());
    return "form";
  }

  @PostMapping("/newOrdem")
  public String saveOrdem(@Valid Manutency manutency, Model model, BindingResult result,
      RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      return "form";
    }
    serviceManutency.createOs(manutency);
    redirectAttributes.addFlashAttribute("message", "Ordem de Serviço criada com sucesso!");

    return "redirect:/ordensServ";
  }

  @GetMapping("edit/{id}")
  public String editOrdem(@PathVariable Long id, Model model) {
    model.addAttribute("ordem", serviceManutency.buscaIdOrdem(id));

    return "form";
  }

  @PostMapping("/delete/{id}")
  public String deleteOrdem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    serviceManutency.deleteOrdem(id);
    redirectAttributes.addFlashAttribute("message", "Ordem excluída com sucesso!");
    return "redirect:/ordensServ";
}

  

}
