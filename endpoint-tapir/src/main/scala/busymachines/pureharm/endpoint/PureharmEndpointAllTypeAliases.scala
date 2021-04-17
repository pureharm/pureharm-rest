package busymachines.pureharm.endpoint

trait PureharmEndpointAllTypeAliases
  extends sttp.tapir.Tapir with aliases.PureharmTapirAliases with aliases.PureharmTapirModelAliases
  with aliases.PureharmTapirServerAliases {}
