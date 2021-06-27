package com.isagron.security.services.token_provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.isagron.security.configuration.properties.JwtProperties;
import com.isagron.security.domain.model.UserPrincipal;
import com.isagron.security.exceptions.VerificationException;
import com.isagron.security.configuration.constant.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider implements TokenProvider{

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaims(userPrincipal);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, jwtProperties.getExpirationTimeInMin());
        Date expiration = calendar.getTime();

        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withAudience(jwtProperties.getAudience())
                .withIssuedAt(now)
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AppConstant.AUTHORITIES, claims)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC512(jwtProperties.getSecret().getBytes()));
    }

    @Override
    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Authentication getAuthentication(String userName, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource()
                .buildDetails(request)
        );
        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean isTokenValid(String userName, String token){
        return StringUtils.hasText(userName) &&
                !isTokenExpired(token);
    }

    @Override
    public String getSubject(String token){
        return getJWTVerifier().verify(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = getJWTVerifier().verify(token).getExpiresAt();
        return expirationDate.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        return getJWTVerifier().verify(token).getClaim(AppConstant.AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());
            return JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
        } catch (JWTVerificationException e){
            throw new VerificationException();
        }
    }

    private String[] getClaims(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }
}
