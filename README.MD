# Som Tracker

Track the [SoM](https://github.com/hackclub/summer-of-making/) shop for changes and send changes to slack.

Built with Scala❤️.

# Running

- Install [SBT](https://www.scala-sbt.org/download/).
- Create a [slack bot](https://api.slack.com/apps/).
- Create config.json.
- Structure your config.json as such:
```json
{
  "signingSecret": "Slack signing secret",
  "botToken": "Token that starts with \"xoxb\"",
  "appToken": "Token that starts with \"xapp\"",
  "cookie": "See instructions below",
  "botChannel": "Slack channel for bot outputs (id)",
  "debugChannel": "Slack channel for debugging (id)",
  "ping": "Slack ping group (id)"
}
```
- Getting your cookie:
- - Open dev tools
- - Navigate to [the shop](https://summer.hackclub.com/shop).
- - Copy the `Cookie` header from the `/shop` request.
- Finally, run the bot using `sbt run`