package br.com.alex.pessoaendereco.exception;

public class EnderecoPrincipalInvalidoException extends RuntimeException {

    public EnderecoPrincipalInvalidoException() {
        super("Deve existir exatamente um endereço principal");
    }
}
