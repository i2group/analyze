package com.example.myclient;

import com.example.generated.invoker.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.*;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.protocol.*;
import org.apache.hc.core5.http.message.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class MyApiClient extends ApiClient {
    private final CookieStore cookieStore = new BasicCookieStore();

    public MyApiClient(String basePath, String username, String password) {
        super.setBasePath(basePath);
        formBasedAuthLogin(username, password);
    }

    private void formBasedAuthLogin(String username, String password) {
        final HttpPost request = new HttpPost(URI.create(getBasePath() + "/j_security_check"));
        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("j_username", username),
                new BasicNameValuePair("j_password", password))));
        final HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        try {
            getHttpClient().execute(request, context, response -> {
                if (response.getCode() < 200 || response.getCode() >= 300) {
                    throw new RuntimeException("Authentication failed: HTTP " + response.getCode());
                }
                return null;
            });
        } catch (IOException ex) {
            throw new RuntimeException("Authentication failed", ex);
        }
        if (cookieStore.getCookies().stream().noneMatch(cookie -> "LtpaToken2".equals(cookie.getName()))) {
            throw new RuntimeException("Authentication failed: no LtpaToken2 cookie received");
        }
    }

    @Override
    public <T> T invokeAPI(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams, String urlQueryDeepObject, Object body, Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept, String contentType, String[] authNames, TypeReference<T> returnType) throws ApiException {
        // Ensure a 401 rather than a 302 if authentication is required
        queryParams.add(new Pair("redirectForAuthentication", "false"));
        cookieStore.getCookies().forEach(cookie -> cookieParams.put(cookie.getName(), cookie.getValue()));
        return super.invokeAPI(path, method, queryParams, collectionQueryParams, urlQueryDeepObject, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
    }
}
