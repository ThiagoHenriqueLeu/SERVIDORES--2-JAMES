package br.com.fatec.catalogo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AUDITORIA_PRODUTO")
public class AuditoriaProdutoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditoria;

    private Long idProduto;

    @Column(nullable = false)
    private String nomeProduto;

    @Column(nullable = false)
    private String campoAlterado;

    @Column(length = 500)
    private String valorAnterior;

    @Column(length = 500)
    private String valorNovo;

    @Column(length = 500)
    private String motivo;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime dataAlteracao;

    public AuditoriaProdutoModel() {
    }

    public AuditoriaProdutoModel(Long idProduto, String nomeProduto, String campoAlterado,
                                 String valorAnterior, String valorNovo, String motivo,
                                 String usuario) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.campoAlterado = campoAlterado;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.motivo = motivo;
        this.usuario = usuario;
        this.dataAlteracao = LocalDateTime.now();
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCampoAlterado() {
        return campoAlterado;
    }

    public void setCampoAlterado(String campoAlterado) {
        this.campoAlterado = campoAlterado;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}
