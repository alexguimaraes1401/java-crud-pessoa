package br.com.alex.pessoaendereco.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PessoaApiIntegrationTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @BeforeEach
        void setUp() {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

    @Test
    void fluxoCompleto_crudPessoa() throws Exception {
        String cpf = "52998224725";

                                String criarPessoaJson = ("""
                                                                {
                                                                        \"nome\": \"Maria\",
                                                                        \"dataNascimento\": \"1990-01-01\",
                                                                        \"cpf\": \"%s\",
                                                                        \"enderecos\": [
                                                                                {
                                                                                        \"rua\": \"Rua A\",
                                                                                        \"numero\": \"10\",
                                                                                        \"bairro\": \"Centro\",
                                                                                        \"cidade\": \"Cidade\",
                                                                                        \"estado\": \"SP\",
                                                                                        \"cep\": \"12345678\",
                                                                                        \"principal\": true
                                                                                }
                                                                        ]
                                                                }
                                                                """).formatted(cpf);

        var createResult = mockMvc.perform(
                post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(criarPessoaJson)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.cpf").value(cpf))
        .andExpect(jsonPath("$.enderecos.length()").value(1))
        .andReturn();

        Number idNumber = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
        long id = idNumber.longValue();

        mockMvc.perform(get("/api/pessoas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.cpf").value(cpf));

        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id));

                                String atualizarPessoaJson = ("""
                                                                {
                                                                        \"nome\": \"Maria Atualizada\",
                                                                        \"dataNascimento\": \"1991-02-02\",
                                                                        \"cpf\": \"%s\",
                                                                        \"enderecos\": [
                                                                                {
                                                                                        \"rua\": \"Rua Atualizada\",
                                                                                        \"numero\": \"55\",
                                                                                        \"bairro\": \"Bairro\",
                                                                                        \"cidade\": \"Cidade\",
                                                                                        \"estado\": \"SP\",
                                                                                        \"cep\": \"87654321\",
                                                                                        \"principal\": true
                                                                                }
                                                                        ]
                                                                }
                                                                """).formatted(cpf);

        mockMvc.perform(
                put("/api/pessoas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(atualizarPessoaJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nome").value("Maria Atualizada"))
        .andExpect(jsonPath("$.cpf").value(cpf))
        .andExpect(jsonPath("$.enderecos[0].rua").value("Rua Atualizada"));

        mockMvc.perform(delete("/api/pessoas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
