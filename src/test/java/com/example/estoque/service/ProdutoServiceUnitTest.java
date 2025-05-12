package com.example.estoque.service;

import com.example.estoque.domain.ItemPedido;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.entity.ProdutoEntity;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceUnitTest {

	@InjectMocks
	private ProdutoService produtoService;

	@Mock
	private ProdutoRepository produtoRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deveCadastrarProduto() {
		Produto produto = new Produto("Camiseta", "Camiseta branca", 49.90, 10);
		produtoService.cadastrarProduto(produto);
		verify(produtoRepository, times(1)).save(any(ProdutoEntity.class));
	}

	@Test
	void deveListarTodosProdutos() {
		ProdutoEntity p1 = new ProdutoEntity(new Produto("Camiseta", "Branca", 30.0, 10));
		ProdutoEntity p2 = new ProdutoEntity(new Produto("Calça", "Jeans", 80.0, 5));
		when(produtoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

		List<Produto> produtos = produtoService.encontrarTodos();
		assertEquals(2, produtos.size());
		assertEquals("Camiseta", produtos.get(0).getNome());
	}

	@Test
	void deveRetornarProdutoPorNome() {
		ProdutoEntity entity = new ProdutoEntity(new Produto("Tênis", "Esportivo", 199.9, 4));
		when(produtoRepository.findByNome("Tênis")).thenReturn(entity);

		Produto produto = produtoService.encontrarPorNome("Tênis");

		assertEquals("Tênis", produto.getNome());
		assertEquals(199.9, produto.getPreco());
	}

	@Test
	void deveAtualizarEstoqueComSucesso() {
		ProdutoEntity entity = new ProdutoEntity(new Produto("Tênis", "Esportivo", 199.9, 10));
		entity.setId(1L);
		when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));

		ItemPedido item = new ItemPedido();
		item.setId(1L);
		item.setQtd(3);
		Pedido pedido = new Pedido();
		pedido.setItens(List.of(item));

		produtoService.atualizarEstoque(pedido);
		verify(produtoRepository).save(entity);
		assertEquals(7, entity.getQtd());
	}

	@Test
	void deveLancarExcecaoQuandoEstoqueInsuficiente() {
		ProdutoEntity entity = new ProdutoEntity(new Produto("Tênis", "Esportivo", 199.9, 2));
		entity.setId(1L);
		when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));

		ItemPedido item = new ItemPedido();
		item.setId(1L);
		item.setQtd(5);
		Pedido pedido = new Pedido();
		pedido.setItens(List.of(item));

		assertThrows(ForaDeEstoqueException.class, () -> produtoService.atualizarEstoque(pedido));
	}
}
