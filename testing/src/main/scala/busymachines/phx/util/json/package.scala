package busymachines.phx.util

package object json
  extends busymachines.pureharm.json.PureharmJsonTypeDefinitions with busymachines.pureharm.json.PureharmJsonImplicits {
  object derive extends busymachines.pureharm.json.GenericSemiAutoDerivation
}
