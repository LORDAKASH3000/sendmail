package com.google_imp;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/oauth2callback")
public class OAuthCallbackServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");

        if (code != null) {
            AuthorizationCodeFlow flow = initializeFlow();
            TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(getRedirectUri(request)).execute();

            String accessToken = tokenResponse.getAccessToken();
            String refreshToken = tokenResponse.getRefreshToken();

            // Store the tokens securely
            // Now you can use these tokens to make authorized API requests

            response.getWriter().write("Authorization successful!");
        } else {
            response.getWriter().write("Authorization failed: No authorization code.");
        }
    }

    private AuthorizationCodeFlow initializeFlow() throws IOException {
        return new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                new GenericUrl("https://accounts.google.com/o/oauth2/token"),
                new ClientParametersAuthentication("381542915521-gi8u4tfps31rdbuv6cgk6e0kmvqfh949.apps.googleusercontent.com", "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"),
                "381542915521-gi8u4tfps31rdbuv6cgk6e0kmvqfh949.apps.googleusercontent.com",
                "https://accounts.google.com/o/oauth2/auth")
                .setScopes(Arrays.asList("https://www.googleapis.com/auth/gmail.readonly"))
                .setDataStoreFactory(getDataStoreFactory())
                .build();
    }

    private String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    private DataStoreFactory getDataStoreFactory() {
        // Return an instance of DataStoreFactory to securely store tokens.
        // Here, we use MemoryDataStoreFactory for a simple in-memory store.
        return MemoryDataStoreFactory.getDefaultInstance();
    }
}