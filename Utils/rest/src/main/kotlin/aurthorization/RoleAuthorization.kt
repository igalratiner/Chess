package aurthorization

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import mu.KLogging
import pojo.TextRole

class RoleAuthorization internal constructor(config: Configuration) {

    constructor(provider: RoleBasedAuthorizer): this(Configuration(provider))

    private var config = config.copy()

    class Configuration internal constructor(val provider: RoleBasedAuthorizer) {

        internal fun copy(): Configuration = Configuration(provider)
    }

    class RoleBasedAuthorizer {
        internal lateinit var authorizationFunction: suspend ApplicationCall.(Set<TextRole>)->Unit

        fun validate(body: suspend ApplicationCall.(Set<TextRole>)-> Unit) {
            authorizationFunction = body
        }
    }

    fun interceptPipeline(pipeline: ApplicationCallPipeline, roles: Set<TextRole>) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, authorizationPhase)
        pipeline.intercept(authorizationPhase) {
            val call = call
            config.provider.authorizationFunction(call, roles)
        }}

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, RoleBasedAuthorizer, RoleAuthorization>, KLogging() {
        private val authorizationPhase = PipelinePhase("authorization")

        override val key: AttributeKey<RoleAuthorization> = AttributeKey("RoleAuthorization")

        @io.ktor.util.KtorExperimentalAPI
        override fun install(
                pipeline: ApplicationCallPipeline,
                configure: RoleBasedAuthorizer.() -> Unit
        ): RoleAuthorization {
            val configuration = RoleBasedAuthorizer().apply(configure)

            return RoleAuthorization(configuration)
        }
    }
}

fun Route.rolesAllowed(vararg roles: TextRole, build: Route.() -> Unit): Route {
    val authorisedRoute = createChild(AuthorisedRouteSelector())
    application.feature(RoleAuthorization).interceptPipeline(this.application, roles.toSet())

    authorisedRoute.build()
    return authorisedRoute
}

class AuthorisedRouteSelector: RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
}