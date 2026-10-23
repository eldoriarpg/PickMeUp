---
title: Commands
sidebar_position: 1
---

# Commands

Pick Me Up has one command, `/pickmeup`. The short form is `/pmu`. None of the sub-commands take arguments.

| Command | Permission | What it does |
|---|---|---|
| `/pickmeup about` | none | Shows the plugin name, authors, version and links. |
| `/pickmeup toggle` | `pickmeup.toggle` | Turns on or off whether other players can pick you up. Players only. |
| `/pickmeup reload` | `pickmeup.reload` | Reads `config.yml` again and applies it. |
| `/pickmeup debug` | `pickmeup.reload` or `eldoutilitites.debug` | Uploads debug data for support. |

## `/pickmeup toggle`

Every player has this permission by default. Each use flips the setting, and the reply says which way it is now:
"Other players can pick you up now." or "Other players can no longer pick you up." The choice is stored on the player,
so it survives logouts and restarts.

Players with `pickmeup.bypass.toggle` can still pick up a player who turned it off.

## `/pickmeup reload`

Use it after you edited `config.yml`. The language, entity list, world list, carry settings and update settings apply
at once.

## `/pickmeup debug`

Support may ask you for this. The command collects data about your server and uploads it to Eldoria, so the
developers can look at your problem. The data includes your installed plugins, the latest log, world names, the
player count and the configuration of Pick Me Up.

The first time you run it, the command explains what is sent and asks you to run it again to agree. After the upload
it shows a link to read the data and a link to delete it. The data is deleted after 14 days.

:::note
The permission `eldoutilitites.debug` is spelled with three "t"s at the end of "utilitites". Use it exactly like that.
:::

## `/pickmeup:eldo-update`

```text
/pickmeup:eldo-update <download|enable-auto|later|never>
```

This command belongs to the update offer in chat, which admins with `pickmeup.updates` get when
`generalSettings.updates.askForAutoUpdate` is on. You normally use it by choosing an option in that offer.

| Argument | What it does |
|---|---|
| `download` | Downloads the new version. It is applied on the next restart. |
| `enable-auto` | Turns on `autoUpdate`, so new versions are downloaded from now on. |
| `later` | Asks again later. |
| `never` | Stops asking. This sets `askForAutoUpdate` to `false`. |

See [`generalSettings`](../configuration.md#generalsettings).
