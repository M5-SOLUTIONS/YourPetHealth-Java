package br.com.yourpethealth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RedirectPorPerfilHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final DestinoPorPerfil destinoPorPerfil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth) throws IOException {
        getRedirectStrategy().sendRedirect(request, response, destinoPorPerfil.urlInicial(auth));
    }
}