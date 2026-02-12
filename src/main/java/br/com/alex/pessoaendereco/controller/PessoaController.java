package br.com.alex.pessoaendereco.controller;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pessoas")
@Tag(name = "Pessoas", description = "Endpoints de Pessoa")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @Operation(summary = "Criar pessoa")
    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(
            @Valid @RequestBody PessoaRequestDTO dto) {

        PessoaResponseDTO response = pessoaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar pessoa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> buscarPorId(@PathVariable Long id) {

        PessoaResponseDTO response = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar pessoas com paginação")
    @GetMapping
    public ResponseEntity<Page<PessoaResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<PessoaResponseDTO> page = pessoaService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Atualizar pessoa")
    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaRequestDTO dto) {

        PessoaResponseDTO response = pessoaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar pessoa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        pessoaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
