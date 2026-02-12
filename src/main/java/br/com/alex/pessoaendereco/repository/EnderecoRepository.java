package br.com.alex.pessoaendereco.repository;

import br.com.alex.pessoaendereco.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByPessoaId(Long pessoaId);
    void deleteByPessoaId(Long pessoaId);
}
