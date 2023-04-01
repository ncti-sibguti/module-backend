package ru.ncti.modulebackend.security.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ncti.modulebackend.security.JwtTokenUtil;
import ru.ncti.modulebackend.security.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        if (requestTokenHeader != null && !requestTokenHeader.isEmpty() && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);

            if (jwtToken.isBlank()) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            } else {
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
