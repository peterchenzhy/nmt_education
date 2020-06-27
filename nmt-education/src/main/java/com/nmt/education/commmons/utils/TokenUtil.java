package com.nmt.education.commmons.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nmt.education.commmons.Consts;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class TokenUtil {

    @Setter
    private static String JWT_KEY;

    private static int EXPIRE_MINUTE = 15 * 60;

    public static String generateToken(Token t) {
        Algorithm algorithm = getAlgorithm();
        String token = JWT.create()
                .withClaim(Consts.LOGIN_USER_HEAD, t.getLoginUserId())
                .withClaim(Consts.ROLE_ID_HEAD, t.getRoleId())
                .withExpiresAt(new Date())
                .sign(algorithm);
        return token;
    }


    public static Token verifyToken(String token) {
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm)
                .acceptExpiresAt(EXPIRE_MINUTE)//6s超时
                .build();
        DecodedJWT jwt = verifier.verify(token);
        Token t = new Token();
        t.setRoleId(jwt.getClaims().get(Consts.ROLE_ID_HEAD).asString());
        t.setLoginUserId(jwt.getClaims().get(Consts.LOGIN_USER_HEAD).asInt());
        return t;
    }

    private static Algorithm getAlgorithm() {
        return Algorithm.HMAC256(JWT_KEY);
    }


    public static void main(String[] args) throws InterruptedException {
        Algorithm algorithm = Algorithm.HMAC256("123");
        String token = JWT.create()
//                .withIssuer("auth0")
                .withClaim("name", "peter")
                .withClaim("roleId", "manager")
                .withExpiresAt(new Date())
                .sign(algorithm);
        System.out.println(token);
        Thread.sleep(5 * 1000);


        JWTVerifier verifier = JWT.require(algorithm)
                .acceptExpiresAt(3)//6s超时
//                .withIssuer("auth0")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        System.out.println(jwt.getClaims().get("name").asString());
        System.out.println(jwt.getClaims().get("roleId").asString());
    }

    @Setter
    @Getter
    public static class Token {
        private String roleId = "";
        private int loginUserId = 0;
    }
}
