package net.brainaxis.onedollar.utils;

import net.brainaxis.onedollar.dto.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthUtils {

    public static String getEmail() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String sub = (String) authenticationToken.getTokenAttributes().get("sub");

        return sub.contains("@")
                ? sub
                : (String) authenticationToken.getTokenAttributes().get("email");
    }


    public static User getUser() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String sub = (String) authenticationToken.getTokenAttributes().get("sub");

        String email = sub.contains("@")
                ? sub
                : (String) authenticationToken.getTokenAttributes().get("email");

        String name = (String) authenticationToken.getTokenAttributes().get("name");
        String picture = (String) authenticationToken.getTokenAttributes().get("picture");

        return User.builder()
                .userName(name)
                .picture(picture)
                .email(email)
                .build();


    }
}
