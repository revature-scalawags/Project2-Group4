# Python Hashtag Scraper

Used to scrape the latest 10,000 tweets with a given hashtag.

## Requirements

- `Python`
- `snscrape` (`pip install snscrape`)

## Usage

From within the python-hashtag-scraper directory:

`python scraper.py [hashtag]`

Only include the hashtag's text; don't pass in the actual `#` character as part of the script's argument.

## Output

Outputs a single `.tsv` file named as `your_hashtag.tsv` (with "your_hashtag" replaced with whatever hashtag you passed in as your argument).

The file is structured with columns of `text`, `username`, and `followers`, each separated by a tab, with each row a different tweet.
