---
title: Commands
sidebar_position: 1
---

# Commands

Pick Me Up has one command, `/pickmeup`. The short form is `/pmu`. None of the sub-commands take arguments.

| Command | Permission | What it does |
|---|---|---|
| `/pickmeup about` | none | Shows the plugin name, authors, version and links. |
| `/pickmeup reload` | `pickmeup.reload` | Reads `config.yml` again and applies the language. |
| `/pickmeup debug` | `pickmeup.reload` or `eldoutilitites.debug` | Uploads debug data for support. |

## `/pickmeup reload`

Use it after you edited `config.yml`. The new entity list, world list, throw force and language apply at once.

## `/pickmeup debug`

Support may ask you for this. The command collects data about your server and uploads it to Eldoria, so the
developers can look at your problem. The data includes your installed plugins, the latest log, world names, the
player count and the configuration of Pick Me Up.

The first time you run it, the command explains what is sent and asks you to run it again to agree. After the upload
it shows a link to read the data and a link to delete it. The data is deleted after 14 days.

:::note
The permission `eldoutilitites.debug` is spelled with three "t"s at the end of "utilitites". Use it exactly like that.
:::
