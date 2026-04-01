package br.com.alex.pessoaendereco.exception;

public class PessoaNaoEncontradaException extends RuntimeException {

    public PessoaNaoEncontradaException() {
        super("Pessoa não encontrada");
    }
}
