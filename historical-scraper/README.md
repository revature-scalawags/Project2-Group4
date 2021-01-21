# Historical Scraper

Scrape historical tweets associated with a given hashtag and dump them into AWS storage, using Twitter's REST API.

Much thanks to Daniela Sfregola for their twitter4s library.

## Usage
Add your consumer and access token as environment variables:

```bash
export TWITTER_CONSUMER_TOKEN_KEY='my-consumer-key'
export TWITTER_CONSUMER_TOKEN_SECRET='my-consumer-secret'
export TWITTER_ACCESS_TOKEN_KEY='my-access-key'
export TWITTER_ACCESS_TOKEN_SECRET='my-access-secret'
```


Run with ```sbt run``` and choose the main to run.
