package br.com.alex.pessoaendereco.controller;
import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.EnderecoResponseDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.service.PessoaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PessoaControllerTest {

    private final PessoaService pessoaService = Mockito.mock(PessoaService.class);
    private final PessoaController controller = new PessoaController(pessoaService);

    @Test
    void criar_deveRetornarCreated() {
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Maria");
        request.setCpf("12345678901");
        request.setDataNascimento(LocalDate.of(1990,1,1));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua A");
        e.setNumero("10");
        e.setBairro("Centro");
        e.setCidade("Cidade");
        e.setEstado("ST");
        e.setCep("12345678");
        e.setPrincipal(true);
        request.setEnderecos(List.of(e));

        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(1L);
        response.setNome("Maria");
        response.setCpf("12345678901");
        EnderecoResponseDTO er = new EnderecoResponseDTO();
        er.setId(2L);
        er.setRua("Rua A");
        er.setNumero("10");
        er.setBairro("Centro");
        er.setCidade("Cidade");
        er.setEstado("ST");
        er.setCep("12345678");
        er.setPrincipal(true);
        response.setEnderecos(List.of(er));

        when(pessoaService.criar(any(PessoaRequestDTO.class))).thenReturn(response);

        var respEntity = controller.criar(request);
        assertEquals(201, respEntity.getStatusCode().value());
        assertEquals(1L, respEntity.getBody().getId());
        assertEquals("Maria", respEntity.getBody().getNome());
    }

    @Test
    void buscarPorId_deveRetornarOk() {
        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(5L);
        response.setNome("Joao");
        response.setCpf("09876543210");
        response.setDataNascimento(LocalDate.of(1985,5,5));
        response.setEnderecos(List.of());

        when(pessoaService.buscarPorId(5L)).thenReturn(response);

        var resp = controller.buscarPorId(5L);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(5L, resp.getBody().getId());
    }

    @Test
    void listar_deveRetornarPage() {
        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(7L);
        response.setNome("Ana");
        response.setCpf("22233344455");
        response.setDataNascimento(LocalDate.of(2000,1,1));
        response.setEnderecos(List.of());

        when(pessoaService.listar(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0,10), 1));

        var resp = controller.listar(PageRequest.of(0,10));
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().getTotalElements());
    }
}
