package br.com.yourpethealth.exception;

import br.com.yourpethealth.dto.response.ApiErroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ErroResponseWriter {

    private final ObjectMapper objectMapper;

    public ErroResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ApiErroResponse montar(HttpStatus status, String codigo, String mensagem,
                                  String path, List<ApiErroResponse.CampoErro> campos) {
        return new ApiErroResponse(LocalDateTime.now(), status.value(), codigo,
                mensagem, path, campos);
    }

    public void escrever(HttpServletResponse response, HttpStatus status,
                         String codigo, String mensagem, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                montar(status, codigo, mensagem, path, null));
    }
}