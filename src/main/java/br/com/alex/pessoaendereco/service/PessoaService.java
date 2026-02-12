package br.com.alex.pessoaendereco.service;
import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.EnderecoResponseDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.entity.Endereco;
import br.com.alex.pessoaendereco.entity.Pessoa;
import br.com.alex.pessoaendereco.repository.EnderecoRepository;
import br.com.alex.pessoaendereco.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PessoaService implements IPessoaService {

    private final PessoaRepository pessoaRepository;
    
    public PessoaService(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public PessoaResponseDTO criar(PessoaRequestDTO dto) {

        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setDataNascimento(dto.getDataNascimento());

        List<Endereco> enderecos = mapEnderecos(dto.getEnderecos(), pessoa);
        validarEnderecoPrincipal(enderecos);

        pessoa.setEnderecos(enderecos);

        Pessoa salva = pessoaRepository.save(pessoa);
        return mapPessoaResponse(salva);
    }

    @Override
    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dto) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setDataNascimento(dto.getDataNascimento());

        pessoa.getEnderecos().clear();

        List<Endereco> novosEnderecos = mapEnderecos(dto.getEnderecos(), pessoa);
        validarEnderecoPrincipal(novosEnderecos);

        pessoa.getEnderecos().addAll(novosEnderecos);

        Pessoa atualizada = pessoaRepository.save(pessoa);
        return mapPessoaResponse(atualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPorId(Long id) {

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        return mapPessoaResponse(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> listar(Pageable pageable) {

        return pessoaRepository.findAll(pageable)
            .map(this::mapPessoaResponse);
    }

    @Override
    public void deletar(Long id) {

        if (!pessoaRepository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada");
        }

        pessoaRepository.deleteById(id);
    }

    private List<Endereco> mapEnderecos(List<EnderecoRequestDTO> dtos, Pessoa pessoa) {
        return dtos.stream().map(dto -> {
            Endereco e = new Endereco();
            e.setRua(dto.getRua());
            e.setNumero(dto.getNumero());
            e.setBairro(dto.getBairro());
            e.setCidade(dto.getCidade());
            e.setEstado(dto.getEstado());
            e.setCep(dto.getCep());
            e.setPrincipal(dto.isPrincipal());
            e.setPessoa(pessoa);
            return e;
        }).collect(Collectors.toList());
    }

    private void validarEnderecoPrincipal(List<Endereco> enderecos) {
        long qtd = enderecos.stream()
            .filter(Endereco::isPrincipal)
            .count();

        if (qtd != 1) {
            throw new RuntimeException("Deve existir exatamente um endereço principal");
        }
    }

    private PessoaResponseDTO mapPessoaResponse(Pessoa pessoa) {

        PessoaResponseDTO dto = new PessoaResponseDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setIdade(pessoa.getIdade());

        List<EnderecoResponseDTO> enderecos = pessoa.getEnderecos()
            .stream()
            .map(e -> {
                EnderecoResponseDTO edto = new EnderecoResponseDTO();
                edto.setId(e.getId());
                edto.setRua(e.getRua());
                edto.setNumero(e.getNumero());
                edto.setBairro(e.getBairro());
                edto.setCidade(e.getCidade());
                edto.setEstado(e.getEstado());
                edto.setCep(e.getCep());
                edto.setPrincipal(e.isPrincipal());
                return edto;
            }).collect(Collectors.toList());

        dto.setEnderecos(enderecos);
        return dto;
    }
}
