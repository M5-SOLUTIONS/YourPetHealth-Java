package br.com.yourpethealth.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class DestinoPorPerfil {

    public String urlInicial(Authentication auth) {
        if (possui(auth, "ROLE_ADMIN")) return "/admin/dashboard";
        if (possui(auth, "ROLE_VETERINARIO")) return "/vet/agenda";
        return "/app/home";
    }

    private boolean possui(Authentication auth, String papel) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(papel));
    }
}