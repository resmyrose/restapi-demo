package com.example.demo.rest.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.demo.rest.util.AuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter implements Filter {

	@Value("${auth.jwt.public.key}")
	private String jwtPublicKey;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (httpRequest.getRequestURI().startsWith("/v1/")) {

			String authorization = httpRequest.getHeader("Authorization");
			if (StringUtils.isEmpty(authorization) || !authorization.startsWith("Bearer ") || authorization.split(" ").length != 2) {
				Map<String, Object> errorDetails = new HashMap<>();
		        errorDetails.put("message", "Missing or malformed Authorization");

		        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

		        mapper.writeValue(httpResponse.getWriter(), errorDetails);
				
				return;
			}
			String token = authorization.split(" ")[1];

			Jws<Claims> claims = AuthUtil.validateJwt(token, jwtPublicKey);
			if (claims == null) {
				Map<String, Object> errorDetails = new HashMap<>();
		        errorDetails.put("message", "Invalid Authorization");

		        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

		        mapper.writeValue(httpResponse.getWriter(), errorDetails);
				
				return;
			}
			// add access level roles to request attributes map
			List<String> roles = (List<String>) claims.getBody().get("roles");
			httpRequest.setAttribute(AuthUtil.ROLES, roles);
			String principal = claims.getBody().get("sub", String.class);
			httpRequest.setAttribute(AuthUtil.PRINCIPAL, principal);
		}
		chain.doFilter(request, response);
	}

}
