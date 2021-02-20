package dev.gressier.osp.services.organization.config

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class SecurityConfig : KeycloakWebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
        http?.run {
            authorizeRequests().anyRequest().authenticated()
            csrf().disable()
        }
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(
            keycloakAuthenticationProvider().apply { setGrantedAuthoritiesMapper(SimpleAuthorityMapper()) }
        )
    }

    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
        RegisterSessionAuthenticationStrategy(SessionRegistryImpl())

    @Bean
    fun keycloakConfigResolver(): KeycloakConfigResolver = KeycloakSpringBootConfigResolver()
}