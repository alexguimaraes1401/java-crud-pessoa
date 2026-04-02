package br.com.alex.pessoaendereco.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public class PessoaRequestDTO {

    @NotBlank
    private String nome;

    @NotNull
    @Past(message = "dataNascimento deve estar no passado")
    private LocalDate dataNascimento;

    @NotBlank
    @CPF
    private String cpf;

    @NotNull
    @Size(min = 1, message = "Deve existir ao menos um endereço")
    private List<EnderecoRequestDTO> enderecos;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<EnderecoRequestDTO> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoRequestDTO> enderecos) {
        this.enderecos = enderecos;
    }
}
