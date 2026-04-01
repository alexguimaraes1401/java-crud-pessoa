package br.com.alex.pessoaendereco.service;

import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.entity.Endereco;
import br.com.alex.pessoaendereco.entity.Pessoa;
import br.com.alex.pessoaendereco.exception.CpfJaCadastradoException;
import br.com.alex.pessoaendereco.exception.EnderecoPrincipalInvalidoException;
import br.com.alex.pessoaendereco.exception.PessoaNaoEncontradaException;
import br.com.alex.pessoaendereco.repository.EnderecoRepository;
import br.com.alex.pessoaendereco.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PessoaServiceTest {

    private PessoaRepository pessoaRepository;
    private EnderecoRepository enderecoRepository;
    private PessoaService pessoaService;

    @BeforeEach
    void setUp() {
        pessoaRepository = Mockito.mock(PessoaRepository.class);
        enderecoRepository = Mockito.mock(EnderecoRepository.class);
        pessoaService = new br.com.alex.pessoaendereco.service.implementation.PessoaService(pessoaRepository, enderecoRepository);
    }

    @Test
    void criar_deveMapearCorretamente() {
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Maria");
        request.setCpf("12345678901");
        request.setDataNascimento(LocalDate.of(1990, 1, 1));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua A");
        e.setNumero("10");
        e.setBairro("Centro");
        e.setCidade("Cidade");
        e.setEstado("ST");
        e.setCep("12345678");
        e.setPrincipal(true);

        request.setEnderecos(List.of(e));

        when(pessoaRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(1L);
            for (Endereco end : p.getEnderecos()) {
                end.setId(2L);
            }
            return p;
        });

        PessoaResponseDTO response = pessoaService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Maria", response.getNome());
        assertEquals("12345678901", response.getCpf());
        assertNotNull(response.getEnderecos());
        assertEquals(1, response.getEnderecos().size());
        assertEquals("Rua A", response.getEnderecos().get(0).getRua());
    }

    @Test
    void listar_deveMapearPagina() {
        Pessoa p = new Pessoa("Joao", "09876543210", LocalDate.of(1985, 5, 5));
        p.setId(5L);
        p.setEnderecos(List.of());

        when(pessoaRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(p)));

        var page = pessoaService.listar(PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Joao", page.getContent().get(0).getNome());
    }

    @Test
    void criar_semEnderecoPrincipal_deveLancarExcecao() {
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Ana");
        request.setCpf("22233344455");
        request.setDataNascimento(LocalDate.of(2000,1,1));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua B");
        e.setNumero("1");
        e.setBairro("Bairro");
        e.setCidade("Cidade");
        e.setEstado("ST");
        e.setCep("87654321");
        e.setPrincipal(false);

        request.setEnderecos(List.of(e));

        when(pessoaRepository.existsByCpf(request.getCpf())).thenReturn(false);

        EnderecoPrincipalInvalidoException ex = assertThrows(EnderecoPrincipalInvalidoException.class, () -> pessoaService.criar(request));
        assertTrue(ex.getMessage().contains("principal"));
    }

    @Test
    void criar_cpfExistente_deveLancarExcecao() {
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Carlos");
        request.setCpf("55566677788");
        request.setDataNascimento(java.time.LocalDate.of(1995, 6, 6));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua X");
        e.setNumero("100");
        e.setBairro("B");
        e.setCidade("C");
        e.setEstado("ST");
        e.setCep("00000000");
        e.setPrincipal(true);
        request.setEnderecos(List.of(e));

        when(pessoaRepository.existsByCpf(request.getCpf())).thenReturn(true);

        CpfJaCadastradoException ex = assertThrows(CpfJaCadastradoException.class, () -> pessoaService.criar(request));
        assertTrue(ex.getMessage().contains("CPF já cadastrado") || ex.getMessage().contains("CPF"));
    }

    @Test
    void atualizar_deveAtualizarCorretamente() {
        Long id = 10L;
        Pessoa pessoaExistente = new Pessoa("Velho", "99988877766", java.time.LocalDate.of(1990, 1, 1));
        pessoaExistente.setId(id);
        pessoaExistente.setEnderecos(new java.util.ArrayList<>());

        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Novo");
        request.setCpf("99988877766");
        request.setDataNascimento(java.time.LocalDate.of(1991,2,2));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua Atualizada");
        e.setNumero("55");
        e.setBairro("Bairro");
        e.setCidade("Cidade");
        e.setEstado("ST");
        e.setCep("11122233");
        e.setPrincipal(true);
        request.setEnderecos(List.of(e));

        when(pessoaRepository.findById(id)).thenReturn(java.util.Optional.of(pessoaExistente));
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resp = pessoaService.atualizar(id, request);

        assertNotNull(resp);
        assertEquals("Novo", resp.getNome());
        assertEquals(1, resp.getEnderecos().size());
        assertEquals("Rua Atualizada", resp.getEnderecos().get(0).getRua());
    }

    @Test
    void atualizar_pessoaNaoEncontrada_deveLancarExcecao() {
        Long id = 99L;
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Qualquer");
        request.setCpf("00011122233");
        request.setDataNascimento(java.time.LocalDate.of(2000,1,1));

        when(pessoaRepository.findById(id)).thenReturn(java.util.Optional.empty());

        PessoaNaoEncontradaException ex = assertThrows(PessoaNaoEncontradaException.class, () -> pessoaService.atualizar(id, request));
        assertTrue(ex.getMessage().contains("Pessoa não encontrada"));
    }
}
