package br.com.yourpethealth.security;

import br.com.yourpethealth.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIXO_BEARER)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = header.substring(PREFIXO_BEARER.length());
            jwtService.extrairEmail(token).ifPresent(email -> autenticar(email, request));
        }

        chain.doFilter(request, response);
    }

    private void autenticar(String email, HttpServletRequest request) {
        try {
            UserDetails usuario = usuarioDetailsService.loadUserByUsername(email);

            // Conta desativada pelo administrador: o token ainda é válido
            // criptograficamente, mas deixa de dar acesso.
            if (!usuario.isEnabled()) {
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (UsernameNotFoundException e) {
            // token válido para usuário removido: segue sem autenticar
        }
    }
}