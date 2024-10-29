package com.group8.projectmanager.services

import com.group8.projectmanager.dtos.UserDto
import com.group8.projectmanager.dtos.token.TokenObtainDto
import com.group8.projectmanager.dtos.token.TokenRefreshResponseDto
import com.group8.projectmanager.models.User
import com.group8.projectmanager.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtsService
@Autowired constructor(

    val jwtEncoder: JwtEncoder,
    val jwtDecoder: JwtDecoder,

    val userService: UserService,
    val userRepository: UserRepository,

    val authenticationProvider: AuthenticationProvider,

    @Value("\${jwts.access-token-lifetime}")
    val accessTokenLifetime: Long,

    @Value("\${jwts.refresh-token-lifetime}")
    val refreshTokenLifetime: Long

) {

    private fun generateToken(user: User, isRefresh: Boolean): Jwt {

        var lifetime = accessTokenLifetime
        if (isRefresh) {
            lifetime = refreshTokenLifetime
        }

        val issued = Instant.now()
        val expiration = issued.plusSeconds(lifetime)

        val claimsSet = JwtClaimsSet.builder()
            .subject(user.username)
            .issuedAt(issued)
            .expiresAt(expiration)
            .build()

        val parameter = JwtEncoderParameters.from(claimsSet)

        return jwtEncoder.encode(parameter)
    }

    fun tokenObtainPair(dto: UserDto): TokenObtainDto {

        val authentication = authenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken(
                dto.username, dto.password
            )
        )

        val user = userService.getUserByAuthentication(authentication)
            .orElseThrow { BadCredentialsException("Principal is not of User type.") }

        return TokenObtainDto(
            generateToken(user, false).tokenValue,
            generateToken(user, true).tokenValue
        )
    }

    fun refreshToken(refresh: String): TokenRefreshResponseDto {

        val claim = jwtDecoder.decode(refresh)
        val user = userRepository.findByUsername(claim.subject)
            .orElseThrow { UsernameNotFoundException("User not found") }

        val newAccessToken = generateToken(user, false).tokenValue
        return TokenRefreshResponseDto(newAccessToken)
    }
}