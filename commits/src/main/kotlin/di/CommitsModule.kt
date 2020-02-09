package di


import org.koin.dsl.module
import org.redisson.Redisson
import org.redisson.RedissonReactive
import org.redisson.config.Config
import services.CommitsService

val CommitsModule = module(createdAtStart = true) {

    single {
        val config = Config()
        config.useSingleServer().address = "redis://0.0.0.0:5120"
        val redisson = Redisson.createReactive(config)
        CommitsService(redisson)
    }
}

