package com.group8.projectmanager.configs

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
class JwtsConfig
@Autowired constructor(

    @Value("\${jwts.public-key}")
    val publicKey: RSAPublicKey,

    @Value("\${jwts.private-key}")
    val privateKey: RSAPrivateKey

) {

    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withPublicKey(publicKey).build()

    @Bean
    fun jwtEncoder(): JwtEncoder {

        val jwk = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .build()

        val jwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))

        return NimbusJwtEncoder(jwkSource)
    }
}