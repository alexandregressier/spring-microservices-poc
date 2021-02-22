package dev.gressier.osp.services.license.config

import io.lettuce.core.ReadFrom.REPLICA_PREFERRED
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@ConfigurationProperties("spring.redis")
@ConstructorBinding
data class RedisConfig(
    val cluster: RedisCluster
) {
    data class RedisCluster(
        val master: RedisNode,
        val replicas: List<RedisNode>,
        val password: String,
    )
    data class RedisNode(
        val host: String,
        val port: Int = 6379,
    )
}

@Configuration
class RedisBeans {

    @Autowired private lateinit var config: RedisConfig

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory(
        config.cluster.run {
            RedisStaticMasterReplicaConfiguration(master.host, master.port).also {
                it.setPassword(password)
                replicas.forEach { replica -> it.addNode(replica.host, replica.port) }
            }
        },
        LettuceClientConfiguration.builder().readFrom(REPLICA_PREFERRED).build()
    )
}