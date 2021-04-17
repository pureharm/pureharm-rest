package busymachines.pureharm

import busymachines.pureharm.effects.Concurrent

package object route {

  @scala.deprecated("Use Http4sRoutes instead. Otherwise it's source compatible", "0.5.0")
  type RestDefs[F[_], ET <: Concurrent[F], RT <: Http4sRuntime[F, ET]] =
    busymachines.pureharm.route.Http4sRoutes[F, ET, RT]

  @scala.deprecated("Use PureharmRouteTypeAliases instead", "0.5.0")
  type PureharmRestHttp4sTypeAliases = PureharmRouteTypeAliases
}
