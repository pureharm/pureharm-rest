package busymachines.pureharm.route

import org.http4s.syntax

trait PureharmRouteAllImplicits
  extends syntax.AllSyntax with syntax.KleisliSyntaxBinCompat0 with syntax.KleisliSyntaxBinCompat1
  with PureharmHttp4sCirceInstances
