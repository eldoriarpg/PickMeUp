[![Release](https://img.shields.io/nexus/maven-releases/de.eldoria/pickmeup?label=Release&logo=Release&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][release]
[![Snapshot](https://img.shields.io/nexus/s/de.eldoria/pickmeup?color=orange&label=Snapshot&server=https%3A%2F%2Feldonexus.de&style=for-the-badge)][snapshot]

[release]: https://eldonexus.de/#browse/browse:maven-releases:de%2Feldoria%2Fpickmeup
[snapshot]: https://eldonexus.de/#browse/browse:maven-snapshots:de%2Feldoria%2Fpickmeup

# Pick Me Up

Pick up mobs, carry them around and throw them.

Sneak and right-click an entity with an empty hand, and it sits on top of you. Walk it somewhere and set it down, or
hold sneak until the power bar fills and send it flying.

- Choose which entities can be picked up, with an allow list or a block list, a permission per entity type and a
  world list.
- Players can be picked up too, and every player can turn that off for themselves with `/pickmeup toggle`.
- Stack carried entities, with an optional height limit.
- Pick up by punching as well, for Bedrock players.
- Respects BentoBox, GriefPrevention, PlotSquared, Towny and RedProtect claims and leaves Citizens NPCs alone.
- Tells admins about new versions and can download them on its own.

Everything else is in the [documentation](https://docs.eldoria.de/pick-me-up).

## Requirements

| What | Version |
|---|---|
| Server | Paper or Folia 1.21.11 or newer |
| Java | 25 |
| Other plugins | None required |

## Download

- [Hangar](https://hangar.papermc.io/eldoriarpg/PickMeUp)
- [Modrinth](https://modrinth.com/plugin/eLiWuFr8)
- [Lyna](https://lyna.eldoria.de/products/5)

## Updating from 1.x

The configuration is converted on the first start. The old file is kept as `config.v1.yml` and the old message
files as `messages.v1`.

## License

Pick Me Up is licensed under the [GNU Affero General Public License v3.0](LICENSE.md).
