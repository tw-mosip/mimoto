package io.mosip.mimoto.security.oauth2;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomAuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String REDIRECT_URI_PARAM = "redirectTo";
    private static final String SESSION_ATTR_REDIRECT = "REDIRECT_URI";

    private final HttpSessionOAuth2AuthorizationRequestRepository delegate =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return delegate.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String redirectTo = request.getParameter(REDIRECT_URI_PARAM);
        if (redirectTo != null) {
            request.getSession().setAttribute(SESSION_ATTR_REDIRECT, redirectTo);
        }

        delegate.saveAuthorizationRequest(authorizationRequest, request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request, HttpServletResponse response) {
        return delegate.removeAuthorizationRequest(request, response);
    }

    public static String getRedirectToFromSession(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(SESSION_ATTR_REDIRECT);
    }
}

