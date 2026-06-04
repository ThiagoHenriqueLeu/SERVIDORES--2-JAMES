package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.services.CategoriaService;
import br.com.fatec.catalogo.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private static final DateTimeFormatter FORMATO_HORARIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Autowired
    private ProdutoService service;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            Model model) {

        if (nome != null && !nome.isBlank()) {
            model.addAttribute("produtos", service.listarPorNome(nome));
        } else if (categoriaId != null) {
            model.addAttribute("produtos", service.listarPorCategoria(categoriaId));
        } else {
            model.addAttribute("produtos", service.listarTodos());
        }

        model.addAttribute("categorias", categoriaService.listarTodas());

        return "lista-produtos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        model.addAttribute("categorias", categoriaService.listarTodas());

        return "cadastro-produto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("produto") ProdutoModel produto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean novo = produto.getIdProduto() == 0;

        if (result.hasErrors()) {
            prepararFormulario(model);
            return viewFormulario(novo);
        }

        try {
            ProdutoModel produtoSalvo = service.salvar(produto);
            redirectAttributes.addFlashAttribute("sucesso", criarMensagemSucesso(produtoSalvo, novo));
        } catch (IllegalArgumentException e) {
            registrarErroDeNegocio(e, result);
            prepararFormulario(model);
            return viewFormulario(novo);
        }

        return "redirect:/produtos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodas());

        return "editar-produto";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Produto excluido com sucesso.");

        return "redirect:/produtos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/auditoria")
    public String auditoria(Model model) {
        model.addAttribute("auditorias", service.listarAuditoria());

        return "auditoria-produtos";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
    }

    private String viewFormulario(boolean novo) {
        return novo ? "cadastro-produto" : "editar-produto";
    }

    private String criarMensagemSucesso(ProdutoModel produto, boolean novo) {
        LocalDateTime dataAtualizacao = produto.getDataCadastro() != null
                ? produto.getDataCadastro()
                : LocalDateTime.now();
        String horario = dataAtualizacao.format(FORMATO_HORARIO);
        String acao = novo ? "cadastrado" : "atualizado";

        return "Produto '" + produto.getNome() + "' " + acao + " com sucesso as " + horario + ".";
    }

    private void registrarErroDeNegocio(IllegalArgumentException e, BindingResult result) {
        String mensagem = e.getMessage();

        if (mensagem != null && mensagem.toLowerCase().contains("quantidade")) {
            result.rejectValue("quantidade", "produto.quantidade.invalida", mensagem);
            return;
        }

        if (mensagem != null && mensagem.toLowerCase().contains("nome")) {
            result.rejectValue("nome", "produto.nome.duplicado", mensagem);
            return;
        }

        if (mensagem != null && mensagem.toLowerCase().contains("motivo")) {
            result.rejectValue("motivoAlteracao", "produto.motivo.obrigatorio", mensagem);
            return;
        }

        result.reject("produto.invalido", mensagem);
    }
}
