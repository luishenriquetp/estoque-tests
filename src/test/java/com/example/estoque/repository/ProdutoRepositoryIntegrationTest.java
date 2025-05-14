package com.example.estoque.repository;

import com.example.estoque.entity.ProdutoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProdutoRepositoryIntegrationTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("givenProduct_whenSave_thenItShouldBePersisted")
    void givenProduct_whenSave_thenItShouldBePersisted() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Notebook");
        produto.setDescricao("Dell XPS");
        produto.setPreco(7000.0);
        produto.setQtd(10);

        ProdutoEntity saved = produtoRepository.save(produto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNome()).isEqualTo("Notebook");
    }

    @Test
    @DisplayName("givenExistingName_whenFindByNome_thenReturnProduct")
    void givenExistingName_whenFindByNome_thenReturnProduct() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Mouse");
        produto.setDescricao("Logitech");
        produto.setPreco(150.0);
        produto.setQtd(20);

        produtoRepository.save(produto);

        ProdutoEntity found = produtoRepository.findByNome("Mouse");

        assertThat(found).isNotNull();
        assertThat(found.getNome()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("givenProducts_whenFindAll_thenReturnAll")
    void givenProducts_whenFindAll_thenReturnAll() {
        ProdutoEntity produto1 = new ProdutoEntity();
        produto1.setNome("Teclado");
        produto1.setDescricao("Mecânico");
        produto1.setPreco(300.0);
        produto1.setQtd(15);

        ProdutoEntity produto2 = new ProdutoEntity();
        produto2.setNome("Monitor");
        produto2.setDescricao("24 polegadas");
        produto2.setPreco(900.0);
        produto2.setQtd(8);

        produtoRepository.save(produto1);
        produtoRepository.save(produto2);

        List<ProdutoEntity> produtos = produtoRepository.findAll();

        assertThat(produtos).hasSize(2);
    }

    @Test
    @DisplayName("givenProduct_whenDelete_thenItShouldBeRemoved")
    void givenProduct_whenDelete_thenItShouldBeRemoved() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Impressora");
        produto.setDescricao("HP Laser");
        produto.setPreco(1200.0);
        produto.setQtd(5);

        ProdutoEntity saved = produtoRepository.save(produto);

        produtoRepository.delete(saved);

        Optional<ProdutoEntity> found = produtoRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
