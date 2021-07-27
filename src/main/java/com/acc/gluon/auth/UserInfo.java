package com.acc.gluon.auth;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import org.eclipse.microprofile.jwt.Claims;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class UserInfo {
    public final long id;
    public final String username;
    public final String personnelNr;

    private UserInfo(long id, String username, String personnelNr) {
        this.id = id;
        this.username = username;
        this.personnelNr = personnelNr;
    }

    public static UserInfo extractFromContext(SecurityContext ctx) {
        var principal = extractPrincipal(ctx);
        try {
            var id= Long.parseLong(principal.getSubject());
            var username = principal.<String>getClaim(Claims.nickname.name());
            var personnelNr = principal.<String>getClaim(Claims.upn.name());
            return new UserInfo(id, username, personnelNr);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("User id (Principal subject) is not a number");
        }
    }

    public static long extractUserIdFromContext(SecurityContext ctx) {
        var principal = extractPrincipal(ctx);
        try {
            return Long.parseLong(principal.getSubject());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("User id (Principal subject) is not a number");
        }
    }

    private static DefaultJWTCallerPrincipal extractPrincipal(SecurityContext ctx) {
        if (!ctx.isSecure()) {
            throw new IllegalStateException("User not authorized");
        }

        Principal caller =  ctx.getUserPrincipal();

        if (caller == null) {
            throw new IllegalStateException("Ctx authorized, but caller is null");
        }

        if (caller instanceof DefaultJWTCallerPrincipal) {
            return (DefaultJWTCallerPrincipal)caller;
        } else {
            throw new IllegalStateException("Authentication type `" + caller.getClass() + "` is not supported");
        }
    }
}
