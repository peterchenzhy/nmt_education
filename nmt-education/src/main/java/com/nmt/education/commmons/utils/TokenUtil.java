package com.nmt.education.commmons.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nmt.education.commmons.Consts;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.auth0.jwt.impl.PublicClaims.EXPIRES_AT;

public class TokenUtil {

    @Setter
    private static String JWT_KEY ;


    /**
     * 超时时间 单位 分钟
     */
    @Setter
    private static int EXPIRE_MINUTE ;
    @Setter
    private static int REFRESH_TOKEN_MINUTE ;

    public static String generateToken(Token t) {
        Algorithm algorithm = getAlgorithm();
        String token = JWT.create()
                .withClaim(Consts.LOGIN_USER_HEAD, t.getLoginUserId())
                .withClaim(Consts.ROLE_ID_HEAD, t.getRoleId())
                .withExpiresAt(DateUtil.addOrSubSomeMinutes(new Date(), EXPIRE_MINUTE))
                .sign(algorithm);
        return token;
    }


    public static Token verifyToken(String token) {
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        Token t = new Token(jwt.getClaims().get(Consts.LOGIN_USER_HEAD).asInt(), jwt.getClaims().get(Consts.ROLE_ID_HEAD).asString());
        t.setJwtToken(token);
        Date expireDate = jwt.getClaims().get(EXPIRES_AT).asDate();
        Date now = new Date();
        if (now.after(expireDate)) {
            throw new RuntimeException("登录过期，请重新登录！");
        } else {
            if (expireDate.getTime() - now.getTime() <= REFRESH_TOKEN_MINUTE * 60 * 1000) {
                t.setJwtToken(generateToken(new Token(t.getLoginUserId(), t.getRoleId())));
            }
        }
        return t;
    }

    private static Algorithm getAlgorithm() {
        return Algorithm.HMAC256(JWT_KEY);
    }


    public static void main(String[] args) throws InterruptedException {
        JWT_KEY = "aa";
        EXPIRE_MINUTE =1 ;
        REFRESH_TOKEN_MINUTE = 1 ;
        Token t = new Token(1, "test");
        String token = generateToken(t);
        System.out.println(new Date()+  "--token: " + token);

        Thread.sleep(10 * 1000);
        Token newToken = verifyToken(token);
        System.out.println(new Date()+"--token: " + newToken.jwtToken);

        Thread.sleep(59 * 1000);
        Token newToken_a = verifyToken(newToken.jwtToken);
        System.out.println(new Date()+"--token: " + newToken_a.jwtToken);


        Token newToken2 = verifyToken(token);
        System.out.println(new Date()+"--token: " + newToken.jwtToken);
    }

    @Setter
    @Getter
    public static class Token {
        private String roleId = "";
        private int loginUserId = 0;
        private String jwtToken;

        public Token(int loginUserId, String roleId) {
            this.loginUserId = loginUserId;
            this.roleId = roleId;

        }
    }
}
