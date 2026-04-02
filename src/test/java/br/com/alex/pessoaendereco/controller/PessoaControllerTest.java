package br.com.alex.pessoaendereco.controller;

import br.com.alex.pessoaendereco.dto.request.EnderecoRequestDTO;
import br.com.alex.pessoaendereco.dto.request.PessoaRequestDTO;
import br.com.alex.pessoaendereco.dto.response.EnderecoResponseDTO;
import br.com.alex.pessoaendereco.dto.response.PessoaResponseDTO;
import br.com.alex.pessoaendereco.exception.ApiExceptionHandler;
import br.com.alex.pessoaendereco.service.PessoaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PessoaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PessoaService pessoaService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        pessoaService = Mockito.mock(PessoaService.class);
        PessoaController controller = new PessoaController(pessoaService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
            .setCustomArgumentResolvers(pageableResolver)
            .build();
    }

    @Test
    void criar_deveRetornarCreated() throws Exception {
        PessoaRequestDTO request = new PessoaRequestDTO();
        request.setNome("Maria");
        request.setCpf("52998224725");
        request.setDataNascimento(LocalDate.of(1990, 1, 1));

        EnderecoRequestDTO e = new EnderecoRequestDTO();
        e.setRua("Rua A");
        e.setNumero("10");
        e.setBairro("Centro");
        e.setCidade("Cidade");
        e.setEstado("SP");
        e.setCep("12345678");
        e.setPrincipal(true);
        request.setEnderecos(List.of(e));

        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(1L);
        response.setNome("Maria");
        response.setCpf("52998224725");
        response.setDataNascimento(LocalDate.of(1990, 1, 1));

        EnderecoResponseDTO er = new EnderecoResponseDTO();
        er.setId(2L);
        er.setRua("Rua A");
        er.setNumero("10");
        er.setBairro("Centro");
        er.setCidade("Cidade");
        er.setEstado("SP");
        er.setCep("12345678");
        er.setPrincipal(true);
        response.setEnderecos(List.of(er));

        when(pessoaService.criar(any(PessoaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(
                post("/api/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Maria"))
            .andExpect(jsonPath("$.cpf").value("52998224725"))
            .andExpect(jsonPath("$.enderecos.length()").value(1))
            .andExpect(jsonPath("$.enderecos[0].id").value(2));
    }

    @Test
    void buscarPorId_deveRetornarOk() throws Exception {
        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(5L);
        response.setNome("Joao");
        response.setCpf("11144477735");
        response.setDataNascimento(LocalDate.of(1985, 5, 5));
        response.setEnderecos(List.of());

        when(pessoaService.buscarPorId(5L)).thenReturn(response);

        mockMvc.perform(get("/api/pessoas/{id}", 5L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.nome").value("Joao"))
            .andExpect(jsonPath("$.cpf").value("11144477735"));
    }

    @Test
    void listar_deveRetornarPage() throws Exception {
        PessoaResponseDTO response = new PessoaResponseDTO();
        response.setId(7L);
        response.setNome("Ana");
        response.setCpf("98765432100");
        response.setDataNascimento(LocalDate.of(2000, 1, 1));
        response.setEnderecos(List.of());

        when(pessoaService.listar(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/pessoas").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].id").value(7))
            .andExpect(jsonPath("$.content[0].nome").value("Ana"));
    }

    @Test
    void criar_invalido_deveRetornar400ComErrosDeValidacao() throws Exception {
        String body = "{" +
            "\"nome\":\"\"," +
            "\"dataNascimento\":null," +
            "\"cpf\":\"123\"," +
            "\"enderecos\":[]" +
            "}";

        mockMvc.perform(
                post("/api/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Erro de validação"))
            .andExpect(jsonPath("$.fieldErrors").exists())
            .andExpect(jsonPath("$.fieldErrors.nome").exists())
            .andExpect(jsonPath("$.fieldErrors.cpf").exists())
            .andExpect(jsonPath("$.fieldErrors.dataNascimento").exists())
            .andExpect(jsonPath("$.fieldErrors.enderecos").exists());
    }
}
