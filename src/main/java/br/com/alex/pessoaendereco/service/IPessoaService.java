package br.com.alex.pessoaendereco.service;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPessoaService {

    PessoaResponseDTO criar(PessoaRequestDTO dto);

    PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dto);

    PessoaResponseDTO buscarPorId(Long id);

    Page<PessoaResponseDTO> listar(Pageable pageable);
    
    void deletar(Long id);
}