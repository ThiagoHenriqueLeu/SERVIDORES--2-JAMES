package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.AuditoriaProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaProdutoRepository extends JpaRepository<AuditoriaProdutoModel, Long> {

    List<AuditoriaProdutoModel> findAllByOrderByDataAlteracaoDesc();
}
