package br.com.aweb.hello_spring_boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @GetMapping
    public String sayHello(){
        return "Olá Mundo Spring Boot!";
    }

    @GetMapping("ola")
    public String sayHelloCustom() {
        return "Olá endpoint especifico!";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam("name") String username){
        return "Olá, " + username + "! Bem-vindo(a)!";
    }

    @GetMapping("/calculator")
    public String calculator(@RequestParam(required = true) double value1, @RequestParam(required =  true) double value2, @RequestParam(required =  false, defaultValue = "soma") String op){

        if(op.equals("subtracao")){
            double result = value1 - value2;
            return "O resultado da subtração de " + value1 + " - " + value2 + " é " + result; 
        }
        else{
            double result = value1 + value2;
            return "O resultado da soma de " + value1 + " + " + value2 + " é " + result; 
        }

    }

    @GetMapping("/message")
    public String printMessageIdiom(@RequestParam(defaultValue = "Visitante", required = false) String username, @RequestParam(defaultValue = "pt", required = false) String idiom){
        if(idiom.equals("en")){
            return "Hello, " + username + "! Welcome.";
        }
        return "Olá, " + username + "! Bem-vindo(a).";
    }

}
