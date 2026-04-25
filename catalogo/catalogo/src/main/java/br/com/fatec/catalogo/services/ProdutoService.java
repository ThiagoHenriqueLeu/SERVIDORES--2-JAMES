package br.com.fatec.catalogo.services;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public List<ProdutoModel> listarTodos() {
        return repository.findAll();
    }

    public List<ProdutoModel> buscarPorNome(String nome) {

        if (nome == null || nome.trim().isEmpty()) {
            return repository.findAll();
        }

        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public ProdutoModel buscarPorId(long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public void salvar(ProdutoModel produto) {

        // 🔥 SE FOR EDIÇÃO
        if (produto.getIdProduto() != 0) {

            boolean existe = repository.existsByNomeAndIdProdutoNot(
                    produto.getNome(),
                    produto.getIdProduto()
            );

            if (existe) {
                throw new IllegalStateException("Produto já existe: " + produto.getNome());
            }

        } else {
            // 🔥 SE FOR NOVO
            boolean existe = repository.existsByNome(produto.getNome());

            if (existe) {
                throw new IllegalStateException("Produto já existe: " + produto.getNome());
            }
        }

        repository.save(produto);
    }

    @Transactional
    public void excluir(long id) {
        repository.deleteById(id);
    }
}