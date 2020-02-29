package di


import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.redisson.Redisson
import org.redisson.RedissonReactive
import org.redisson.config.Config
import rest.CommitShipper
import services.CommitsService

val CommitsModule = module(createdAtStart = true) {

    single {
        val config = Config()
        config.useSingleServer().address = "redis://0.0.0.0:5120"
        val redisson = Redisson.create(config)
        CommitsService(redisson)
    }

    factory {
        (textHash: String, channel: SendChannel<Frame>) -> CommitShipper(textHash, channel)
    }
}

