package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
}