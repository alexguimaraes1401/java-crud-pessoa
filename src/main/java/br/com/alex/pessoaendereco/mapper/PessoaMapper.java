package br.com.alex.pessoaendereco.mapper;

import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.entity.Pessoa;

public final class PessoaMapper {

    private PessoaMapper() {
    }

    public static Pessoa newEntity(PessoaRequestDTO dto) {
        return new Pessoa(dto.getNome(), dto.getCpf(), dto.getDataNascimento());
    }

    public static void updateEntity(Pessoa pessoa, PessoaRequestDTO dto) {
        pessoa.setNome(dto.getNome());
        pessoa.setDataNascimento(dto.getDataNascimento());
    }

    public static PessoaResponseDTO toResponseDTO(Pessoa pessoa) {
        PessoaResponseDTO dto = new PessoaResponseDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setIdade(pessoa.getIdade());
        dto.setEnderecos(EnderecoMapper.toResponseDTOList(pessoa.getEnderecos()));
        return dto;
    }
}
