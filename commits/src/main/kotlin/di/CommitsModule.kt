package di


import org.koin.dsl.module
import org.koin.experimental.builder.single
import rest.CommitsResource
import services.CommitsService

val CommitsModule = module(createdAtStart = true) {
    single<CommitsResource>()
    single<CommitsService>()
}

