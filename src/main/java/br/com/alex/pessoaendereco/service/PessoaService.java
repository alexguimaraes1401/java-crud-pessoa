package br.com.alex.pessoaendereco.service;

import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.entity.Endereco;
import br.com.alex.pessoaendereco.entity.Pessoa;
import br.com.alex.pessoaendereco.exception.CpfJaCadastradoException;
import br.com.alex.pessoaendereco.exception.PessoaNaoEncontradaException;
import br.com.alex.pessoaendereco.mapper.EnderecoMapper;
import br.com.alex.pessoaendereco.mapper.PessoaMapper;
import br.com.alex.pessoaendereco.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public PessoaResponseDTO criar(PessoaRequestDTO dto) {

        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException();
        }

        Pessoa pessoa = PessoaMapper.newEntity(dto);
        List<Endereco> enderecos = EnderecoMapper.toEntityList(dto.getEnderecos(), pessoa);

        pessoa.setEnderecos(enderecos);
        pessoa.validarEnderecoPrincipal();

        Pessoa salva = pessoaRepository.save(pessoa);
        return PessoaMapper.toResponseDTO(salva);
    }

    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dto) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(PessoaNaoEncontradaException::new);

        PessoaMapper.updateEntity(pessoa, dto);
        mergeEnderecos(pessoa, dto.getEnderecos());
        pessoa.validarEnderecoPrincipal();

        Pessoa atualizada = pessoaRepository.save(pessoa);
        return PessoaMapper.toResponseDTO(atualizada);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorId(Long id) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(PessoaNaoEncontradaException::new);

        return PessoaMapper.toResponseDTO(pessoa);
    }

    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> listar(Pageable pageable) {

        return pessoaRepository.findAll(pageable)
            .map(PessoaMapper::toResponseDTO);
    }
    
    public void deletar(Long id) {

        if (!pessoaRepository.existsById(id)) {
            throw new PessoaNaoEncontradaException();
        }

        pessoaRepository.deleteById(id);
    }

    private void mergeEnderecos(Pessoa pessoa, List<EnderecoRequestDTO> dtos) {
        List<Endereco> atuais = pessoa.getEnderecos();

        Map<Long, Endereco> porId = new HashMap<>();
        Map<String, Endereco> porChave = new HashMap<>();
        for (Endereco endereco : atuais) {
            if (endereco.getId() != null) {
                porId.put(endereco.getId(), endereco);
            }
            porChave.put(chave(endereco), endereco);
        }

        Set<Endereco> manter = new HashSet<>();

        for (EnderecoRequestDTO dto : dtos) {
            Endereco endereco = null;
            if (dto.getId() != null) {
                endereco = porId.get(dto.getId());
            }
            if (endereco == null) {
                endereco = porChave.get(chave(dto));
            }
            if (endereco == null) {
                endereco = EnderecoMapper.toEntity(dto, pessoa);
                atuais.add(endereco);
            } else {
                EnderecoMapper.updateEntity(endereco, dto, pessoa);
            }

            manter.add(endereco);
        }

        atuais.removeIf(endereco -> !manter.contains(endereco));
    }

    private String chave(EnderecoRequestDTO dto) {
        return dto.getCep() + "|" + dto.getRua() + "|" + dto.getNumero();
    }

    private String chave(Endereco endereco) {
        return endereco.getCep() + "|" + endereco.getRua() + "|" + endereco.getNumero();
    }
}