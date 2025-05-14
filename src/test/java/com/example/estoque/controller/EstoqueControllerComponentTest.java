package com.example.estoque.controller;

import com.example.estoque.domain.ItemPedido;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstoqueController.class)
public class EstoqueControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidProduct_whenPostToEstoque_thenShouldReturnSuccessMessage() throws Exception {
        Produto produto = new Produto("Camiseta", "Branca", 29.9, 10);

        mockMvc.perform(post("/estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cadastrado com Sucesso"));
    }

    @Test
    void givenProductsInService_whenGetEstoque_thenShouldReturnProductList() throws Exception {
        Produto produto = new Produto("Tênis", "Corrida", 199.9, 4);
        Mockito.when(produtoService.encontrarTodos()).thenReturn(List.of(produto));

        mockMvc.perform(get("/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Tênis"));
    }

    @Test
    void givenProductName_whenGetEstoqueByName_thenShouldReturnProduct() throws Exception {
        Produto produto = new Produto("Tênis", "Corrida", 199.9, 4);
        Mockito.when(produtoService.encontrarPorNome("Tênis")).thenReturn(produto);

        mockMvc.perform(get("/estoque/Tênis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tênis"));
    }

    @Test
    void givenValidOrder_whenPostToUpdateEstoque_thenShouldReturnStockUpdatedMessage() throws Exception {
        Pedido pedido = new Pedido();
        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setQtd(2);
        pedido.setItens(List.of(item));

        mockMvc.perform(post("/estoque/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(content().string("Estoque Atualizado"));
    }

    @Test
    void givenInsufficientStock_whenPostToUpdateEstoque_thenShouldReturnBadRequestWithErrorMessage() throws Exception {
        Pedido pedido = new Pedido();
        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setQtd(99);
        pedido.setItens(List.of(item));

        Mockito.doThrow(new ForaDeEstoqueException("Estoque insuficiente"))
                .when(produtoService).atualizarEstoque(any(Pedido.class));

        mockMvc.perform(post("/estoque/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Estoque insuficiente"));
    }
}
