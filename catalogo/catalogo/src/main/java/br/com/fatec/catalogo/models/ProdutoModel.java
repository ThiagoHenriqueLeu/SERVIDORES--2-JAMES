package br.com.fatec.catalogo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PRODUTO")
public class ProdutoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProduto;

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser um número positivo.")
    private BigDecimal valor;

    @Column(name = "data_cadastro", updatable = false, nullable = false)
    private LocalDateTime dataCadastro;


    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaModel categoria;

    // Método de ciclo de vida JPA
    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
    }

    // Construtor padrão
    public ProdutoModel() {}

    // Getters e Setters
    public long getIdProduto() { return idProduto; }
    public void setIdProduto(long idProduto) { this.idProduto = idProduto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }


    public CategoriaModel getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaModel categoria) {
        this.categoria = categoria;
    }
}