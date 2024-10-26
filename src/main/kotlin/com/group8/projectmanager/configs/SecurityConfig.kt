package com.group8.projectmanager.configs

import com.group8.projectmanager.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig
@Autowired constructor(val userRepository: UserRepository) {

    fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found") }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {

            csrf { disable() }

            cors {
                configurationSource = CorsConfigurationSource { _ ->

                    val configuration = CorsConfiguration()

                    HttpMethod.values()
                        .forEach(configuration::addAllowedMethod)

                    configuration.applyPermitDefaultValues()
                }
            }

            authorizeHttpRequests {

                authorize("/api/invitations/**", authenticated)
                authorize("/api/projects/**", authenticated)

                authorize(anyRequest, permitAll)
            }

            httpBasic { }

            oauth2ResourceServer {
                jwt {

                    jwtAuthenticationConverter = Converter { source ->

                        val username = source.subject
                        val user = loadUserByUsername(username)

                        UsernamePasswordAuthenticationToken(
                            user, source, user.authorities
                        )
                    }
                }
            }
        }

        return http.build()
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {

        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(this::loadUserByUsername)
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
}