package br.com.alex.pessoaendereco.mapper;

import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.response.EnderecoResponseDTO;
import br.com.alex.pessoaendereco.entity.Endereco;
import br.com.alex.pessoaendereco.entity.Pessoa;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class EnderecoMapper {

    private EnderecoMapper() {
    }

    public static List<Endereco> toEntityList(List<EnderecoRequestDTO> dtos, Pessoa pessoa) {
        if (dtos == null) {
            return Collections.emptyList();
        }

        return dtos.stream()
            .map(dto -> toEntity(dto, pessoa))
            .collect(Collectors.toList());
    }

    public static Endereco toEntity(EnderecoRequestDTO dto, Pessoa pessoa) {
        return new Endereco(
                dto.getRua(),
                dto.getNumero(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                dto.getCep(),
                dto.isPrincipal(),
                pessoa
        );
    }

    public static EnderecoResponseDTO toResponseDTO(Endereco e) {
        EnderecoResponseDTO dto = new EnderecoResponseDTO();
        dto.setId(e.getId());
        dto.setRua(e.getRua());
        dto.setNumero(e.getNumero());
        dto.setBairro(e.getBairro());
        dto.setCidade(e.getCidade());
        dto.setEstado(e.getEstado());
        dto.setCep(e.getCep());
        dto.setPrincipal(e.isPrincipal());
        return dto;
    }

    public static List<EnderecoResponseDTO> toResponseDTOList(List<Endereco> enderecos) {
        if (enderecos == null) {
            return Collections.emptyList();
        }

        return enderecos.stream()
            .map(EnderecoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
}
