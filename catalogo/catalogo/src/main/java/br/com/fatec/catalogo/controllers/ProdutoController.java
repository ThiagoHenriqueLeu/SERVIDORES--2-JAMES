package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.services.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    // 🔥 NOVO: precisa para carregar categorias no formulário
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listar(@RequestParam(required = false) String nome, Model model) {
        model.addAttribute("produtos", service.buscarPorNome(nome));
        return "lista-produtos";
    }

    // ✅ FORMULÁRIO NOVO
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        model.addAttribute("categorias", categoriaRepository.findAll()); // 🔥 IMPORTANTE
        return "cadastro-produto";
    }

    // ✅ SALVAR
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") ProdutoModel produto,
                         BindingResult result,
                         Model model) {

        // 🔥 se der erro, precisa recarregar categorias
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "cadastro-produto";
        }

        try {
            service.salvar(produto);
        } catch (IllegalStateException e) {
            result.rejectValue("nome", "erro.nome", e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "cadastro-produto";
        }

        return "redirect:/produtos";
    }

    // ✅ EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable long id, Model model) {

        ProdutoModel produto = service.buscarPorId(id);

        if (produto == null) {
            return "redirect:/produtos";
        }

        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaRepository.findAll());

        return "cadastro-produto";
    }

    // ✅ EXCLUIR
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable long id) {

        System.out.println("🔥 EXCLUINDO ID: " + id);

        service.excluir(id);

        return "redirect:/produtos";
    }
}