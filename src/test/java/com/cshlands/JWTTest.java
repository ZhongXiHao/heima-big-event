package com.cshlands;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cshlands.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JWTTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGen() {


        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("name", "test");
        System.out.println(jwtUtil.genToken(claims));
    }

    @Test
    public void parse(){
        System.out.println(jwtUtil.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGFpbXMiOnsibmFtZSI6InRlc3QiLCJpZCI6MX0sImV4cCI6MTc5MDUyMzkyNX0.A0u2GHM4--iucQoQimWZggnZVtfkhQF3oauZiOAUcJg"));
    }
}
