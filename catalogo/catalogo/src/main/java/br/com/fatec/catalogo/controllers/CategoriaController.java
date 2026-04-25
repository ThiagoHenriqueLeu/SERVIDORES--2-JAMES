package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.CategoriaModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.repositories.ProdutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private ProdutoRepository produtoRepository; // ✅ AGORA NO LUGAR CERTO

    // 🔥 TELA
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", repository.findAll());
        model.addAttribute("categoria", new CategoriaModel());
        return "categorias";
    }

    // 🔥 SALVAR FORM
    @PostMapping("/salvar-form")
    public String salvarForm(@ModelAttribute CategoriaModel categoria) {
        repository.save(categoria);
        return "redirect:/categorias";
    }

    // 🔥 EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categorias", repository.findAll());
        model.addAttribute("categoria", repository.findById(id).orElse(new CategoriaModel()));
        return "categorias";
    }

    // 🔥 EXCLUIR COM VALIDAÇÃO
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {

        boolean temProduto = produtoRepository.existsByCategoriaId(id);

        if (temProduto) {
            return "redirect:/categorias?erro=temProduto";
        }

        repository.deleteById(id);
        return "redirect:/categorias";
    }

    // 🔥 API (modal do produto)
    @PostMapping("/salvar")
    @ResponseBody
    public CategoriaModel salvarApi(@RequestBody CategoriaModel categoria) {
        return repository.save(categoria);
    }
}