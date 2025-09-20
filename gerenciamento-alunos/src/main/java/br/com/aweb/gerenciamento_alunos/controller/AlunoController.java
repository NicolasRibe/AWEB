package br.com.aweb.gerenciamento_alunos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.aweb.gerenciamento_alunos.model.Aluno;
import br.com.aweb.gerenciamento_alunos.service.AlunosService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    AlunosService alunosService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("alunos", alunosService.buscarAlunos());
        return "list";
    }

    @GetMapping("/new")
    public String criarAluno(Model model) {
        model.addAttribute("aluno", new Aluno());

        return "form";
    }

    @PostMapping
    public String save(@Valid Aluno aluno, BindingResult result, RedirectAttributes atributes) {
        if (result.hasErrors())
            return "form";
        alunosService.addAluno(aluno);
        atributes.addFlashAttribute("message", "Aluno salvo com sucesso !!");
        return "redirect:/alunos";
    }

    @GetMapping("/edit/{id}")
    public String editaAluno(Model model, @PathVariable Long id, RedirectAttributes attributes) {
        try {
            model.addAttribute("aluno", alunosService.buscarId(id));
            return "form";
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/alunos";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {

        try {
            alunosService.deleteAluno(id);
            attributes.addFlashAttribute("message", "Aluno excluido com sucesso !");

        } catch (Exception e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/alunos";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String nome, Model model) {
        if (nome != null && !nome.isBlank()) {
            model.addAttribute("alunos", alunosService.buscaAlunoNome(nome));
        }
        return "search";
    }

}
