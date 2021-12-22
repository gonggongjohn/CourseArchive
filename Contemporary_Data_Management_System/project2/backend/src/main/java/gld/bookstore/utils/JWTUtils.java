package gld.bookstore.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import gld.bookstore.controller.dto.LoginBody;

import java.util.Calendar;

public class JWTUtils {
    private static final String SECRET="gld-bookstore-*%#@*!&";

    public static String generateToken(LoginBody loginBody){
        JWTCreator.Builder builder= JWT.create();
        builder.withClaim("name",loginBody.getName());
        builder.withClaim("terminal",loginBody.getTerminal());
        Calendar instance=Calendar.getInstance();
        instance.add(Calendar.HOUR,24);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    public static DecodedJWT getTokenInfo(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }
}
