package com.example.gymapp.service

import com.example.gymapp.model.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    val secret = "7bace0d50a22258fea65936973a3a4e139e6784c406e9192004169f037e98d15"
    fun generateToken(user: Member): String{
        var token: String = Jwts
            .builder()
            .subject(user.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 24*60*60*1000))
            .signWith(getSigninKey())
            .compact()
        return token
    }
    fun extractAllClaims(token: String): Claims {
        return Jwts
            .parser()
            .verifyWith(getSigninKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
    fun <T> extractClaim(token: String, resolver: (claim: Claims) -> T): T{
        val claims: Claims = extractAllClaims(token)
        return resolver(claims)
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun getSigninKey(): SecretKey {
        val keyBytes = Decoders.BASE64URL.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun isValid(token: String, user: UserDetails): Boolean{
        val username: String = extractUsername(token)
        return username == user.username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean{
        return extractExpiration(token).before(Date())
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }
}