package di

import org.koin.dsl.module
import org.koin.experimental.builder.single
import service.ServerService

val serviceModule = module(createdAtStart = true) {
    single<ServerService>()
}