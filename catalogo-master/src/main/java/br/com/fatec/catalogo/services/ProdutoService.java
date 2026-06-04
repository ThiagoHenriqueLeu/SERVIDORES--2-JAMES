package br.com.fatec.catalogo.services;

import br.com.fatec.catalogo.models.AuditoriaProdutoModel;
import br.com.fatec.catalogo.models.CategoriaModel;
import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.AuditoriaProdutoRepository;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private AuditoriaProdutoRepository auditoriaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<ProdutoModel> listarTodos() {
        return repository.findAll();
    }

    public List<ProdutoModel> listarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public ProdutoModel buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado: " + id));
    }

    public List<ProdutoModel> listarPorCategoria(Long idCategoria) {
        return repository.findByCategoriaIdCategoria(idCategoria);
    }

    public List<AuditoriaProdutoModel> listarAuditoria() {
        return auditoriaRepository.findAllByOrderByDataAlteracaoDesc();
    }

    @Transactional
    public ProdutoModel salvar(ProdutoModel produto) {
        ProdutoModel produtoAtual = produto.getIdProduto() == 0
                ? null
                : buscarPorId(produto.getIdProduto());

        validarProduto(produto, produtoAtual);
        List<AuditoriaProdutoModel> auditorias = montarAuditorias(produtoAtual, produto);

        produto.setDataCadastro(LocalDateTime.now());

        ProdutoModel produtoSalvo = repository.save(produto);

        if (!auditorias.isEmpty()) {
            auditoriaRepository.saveAll(auditorias);
        }

        return produtoSalvo;
    }

    @Transactional
    public void excluir(long id) {
        repository.deleteById(id);
    }

    private void validarProduto(ProdutoModel produto, ProdutoModel produtoAtual) {
        if (produto.getIdProduto() == 0 && repository.existsByNome(produto.getNome())) {
            throw new IllegalArgumentException("Ja existe um produto com este nome.");
        }

        if (produto.getQuantidade() == null) {
            throw new IllegalArgumentException("A quantidade e obrigatoria.");
        }

        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade nao pode ser negativa.");
        }

        if (produtoAtual != null && !alterouPrecoOuEstoque(produto, produtoAtual)) {
            produto.setMotivoAlteracao(produtoAtual.getMotivoAlteracao());
        }
    }

    private boolean alterouPrecoOuEstoque(ProdutoModel produto, ProdutoModel produtoAtual) {
        boolean alterouPreco = compararDecimal(produto.getValor(), produtoAtual.getValor()) != 0;
        boolean alterouQuantidade = !produto.getQuantidade().equals(produtoAtual.getQuantidade());

        return alterouPreco || alterouQuantidade;
    }

    private int compararDecimal(BigDecimal valorNovo, BigDecimal valorAtual) {
        if (valorNovo == null && valorAtual == null) {
            return 0;
        }

        if (valorNovo == null || valorAtual == null) {
            return -1;
        }

        return valorNovo.compareTo(valorAtual);
    }

    private boolean textoVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private List<AuditoriaProdutoModel> montarAuditorias(ProdutoModel produtoAtual, ProdutoModel produtoNovo) {
        List<AuditoriaProdutoModel> auditorias = new ArrayList<>();

        if (produtoAtual == null) {
            return auditorias;
        }

        String usuario = usuarioLogado();
        String motivo = textoVazio(produtoNovo.getMotivoAlteracao()) ? "-" : produtoNovo.getMotivoAlteracao().trim();
        Long idProduto = produtoAtual.getIdProduto();
        String nomeProduto = produtoNovo.getNome();

        if (!Objects.equals(produtoAtual.getNome(), produtoNovo.getNome())) {
            auditorias.add(novaAuditoria(idProduto, nomeProduto, "Nome",
                    produtoAtual.getNome(), produtoNovo.getNome(), motivo, usuario));
        }

        if (compararDecimal(produtoNovo.getValor(), produtoAtual.getValor()) != 0) {
            auditorias.add(novaAuditoria(idProduto, nomeProduto, "Preco",
                    formatarDecimal(produtoAtual.getValor()), formatarDecimal(produtoNovo.getValor()), motivo, usuario));
        }

        if (!Objects.equals(produtoAtual.getQuantidade(), produtoNovo.getQuantidade())) {
            auditorias.add(novaAuditoria(idProduto, nomeProduto, "Estoque",
                    texto(produtoAtual.getQuantidade()), texto(produtoNovo.getQuantidade()), motivo, usuario));
        }

        if (!Objects.equals(idCategoria(produtoAtual.getCategoria()), idCategoria(produtoNovo.getCategoria()))) {
            auditorias.add(novaAuditoria(idProduto, nomeProduto, "Categoria",
                    nomeCategoria(produtoAtual.getCategoria()), nomeCategoria(produtoNovo.getCategoria()), motivo, usuario));
        }

        return auditorias;
    }

    private AuditoriaProdutoModel novaAuditoria(Long idProduto, String nomeProduto, String campo,
                                                String valorAnterior, String valorNovo,
                                                String motivo, String usuario) {
        return new AuditoriaProdutoModel(idProduto, nomeProduto, campo, valorAnterior, valorNovo, motivo, usuario);
    }

    private String usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "Sistema";
        }

        return authentication.getName();
    }

    private Long idCategoria(CategoriaModel categoria) {
        return categoria == null ? null : categoria.getIdCategoria();
    }

    private String nomeCategoria(CategoriaModel categoria) {
        if (categoria == null || categoria.getIdCategoria() == null) {
            return "Sem categoria";
        }

        if (!textoVazio(categoria.getNome())) {
            return categoria.getNome();
        }

        return categoriaRepository.findById(categoria.getIdCategoria())
                .map(CategoriaModel::getNome)
                .orElse("Categoria #" + categoria.getIdCategoria());
    }

    private String formatarDecimal(BigDecimal valor) {
        return valor == null ? "-" : valor.toPlainString();
    }

    private String texto(Object valor) {
        return valor == null ? "-" : valor.toString();
    }
}
