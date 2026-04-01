package br.com.alex.pessoaendereco.service.implementation;

import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.entity.Endereco;
import br.com.alex.pessoaendereco.entity.Pessoa;
import br.com.alex.pessoaendereco.exception.CpfJaCadastradoException;
import br.com.alex.pessoaendereco.exception.EnderecoPrincipalInvalidoException;
import br.com.alex.pessoaendereco.exception.PessoaNaoEncontradaException;
import br.com.alex.pessoaendereco.mapper.EnderecoMapper;
import br.com.alex.pessoaendereco.mapper.PessoaMapper;
import br.com.alex.pessoaendereco.repository.EnderecoRepository;
import br.com.alex.pessoaendereco.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PessoaService implements br.com.alex.pessoaendereco.service.PessoaService {

    private final PessoaRepository pessoaRepository;
    
    public PessoaService(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public PessoaResponseDTO criar(PessoaRequestDTO dto) {

        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException();
        }

        Pessoa pessoa = PessoaMapper.newEntity(dto);

        List<Endereco> enderecos = EnderecoMapper.toEntityList(dto.getEnderecos(), pessoa);
        validarEnderecoPrincipal(enderecos);

        pessoa.setEnderecos(enderecos);

        Pessoa salva = pessoaRepository.save(pessoa);
        return PessoaMapper.toResponseDTO(salva);
    }

    @Override
    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dto) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(PessoaNaoEncontradaException::new);

        PessoaMapper.updateEntity(pessoa, dto);

        pessoa.getEnderecos().clear();

        List<Endereco> novosEnderecos = EnderecoMapper.toEntityList(dto.getEnderecos(), pessoa);
        validarEnderecoPrincipal(novosEnderecos);

        pessoa.getEnderecos().addAll(novosEnderecos);

        Pessoa atualizada = pessoaRepository.save(pessoa);
        return PessoaMapper.toResponseDTO(atualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorId(Long id) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(PessoaNaoEncontradaException::new);

        return PessoaMapper.toResponseDTO(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> listar(Pageable pageable) {

        return pessoaRepository.findAll(pageable)
            .map(PessoaMapper::toResponseDTO);
    }

    @Override
    public void deletar(Long id) {

        if (!pessoaRepository.existsById(id)) {
            throw new PessoaNaoEncontradaException();
        }

        pessoaRepository.deleteById(id);
    }

    private void validarEnderecoPrincipal(List<Endereco> enderecos) {
        long qtd = enderecos.stream()
            .filter(Endereco::isPrincipal)
            .count();

        if (qtd != 1) {
            throw new EnderecoPrincipalInvalidoException();
        }
    }
}
