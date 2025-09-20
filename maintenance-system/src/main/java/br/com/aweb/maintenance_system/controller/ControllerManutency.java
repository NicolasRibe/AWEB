package br.com.aweb.maintenance_system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import br.com.aweb.maintenance_system.service.ServiceManutency;


@Controller
@RequestMapping("/ordensServ")
public class ControllerManutency {

    @Autowired
    ServiceManutency serviceManutency;

  @GetMapping("/create")
  public ModelAndView home(){
    return new ModelAndView("home", Map.of("ordemService", serviceManutency.findAll(Sort.by("cr"))))

  }

    
}
