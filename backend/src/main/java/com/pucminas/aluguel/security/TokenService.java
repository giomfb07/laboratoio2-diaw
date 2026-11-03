package com.pucminas.aluguel.security;

import com.pucminas.aluguel.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final long expiracaoMs;

    public TokenService(JwtEncoder encoder, @Value("${app.jwt.expiration}") long expiracaoMs) {
        this.encoder = encoder;
        this.expiracaoMs = expiracaoMs;
    }

    public String gerar(Usuario u) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(u.getLogin())
                .issuedAt(agora)
                .expiresAt(agora.plus(Duration.ofMillis(expiracaoMs)))
                .claim("perfil", u.getPerfil().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
