package com.example.app.filter;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilter implements Filter {
	
	private static final List<String> EXCLUSION_URLS = List.of(
	    "/user/add",
	    "/user/add/complete",
	    "/user/delete",
	    "/user/delete/complete"
	);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		if(EXCLUSION_URLS.contains(req.getRequestURI())) {
			chain.doFilter(request, response);
			return;
		}
		
		if(session.getAttribute("userId") == null) {
			res.sendRedirect("/");
			return;
		}
		
		chain.doFilter(request, response);
	}

}
