package com.example.myclient;

import com.example.generated.invoker.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MyApiClient extends ApiClient {
    private final CookieStore cookieStore = new BasicCookieStore();

    public MyApiClient(String basePath, String username, String password) {
        super.setBasePath(basePath);
        formBasedAuthLogin(username, password);
    }

    private void formBasedAuthLogin(String username, String password) {
        final String contentType = "application/x-www-form-urlencoded";
        final Map<String, Object> formParams = new HashMap<>();
        formParams.put("j_username", username);
        formParams.put("j_password", password);
        try {
            invokeAPI("/j_security_check", "POST", new ArrayList<>(), new ArrayList<>(), null, new HashMap<>(), new HashMap<>(), formParams, null, contentType, new String[0], null);
        } catch (ApiException ex) {
            if (ex.getCode() == 302) {
                final List<String> setCookies = ex.getResponseHeaders().get("Set-Cookie");
                if (setCookies != null && !setCookies.isEmpty()) {
                    setCookies.forEach(this::setCookie);
                    return;
                }
            }
            throw new RuntimeException("Authentication failed", ex);
        }
    }

    private void setCookie(String setCookie) {
        final int splitAt = setCookie.indexOf('=');
        final String cookieName = setCookie.substring(0, splitAt);
        final String cookieValue = setCookie.substring(splitAt + 1, setCookie.indexOf(";"));
        cookieStore.addCookie(buildCookie(cookieName, cookieValue, getUri()));
    }

    private URI getUri() {
        try {
            return new URI(super.getBasePath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T invokeAPI(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept, String contentType, String[] authNames, TypeReference<T> returnType) throws ApiException {
        // Ensure a 401 rather than a 302 if authentication is required
        queryParams.add(new Pair("redirectForAuthentication", "false"));
        cookieStore.getCookies().forEach(cookie -> cookieParams.put(cookie.getName(), cookie.getValue()));
        return super.invokeAPI(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
    }
}
