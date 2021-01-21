import snscrape.modules.twitter as sntwitter
import sys

hashtag = sys.argv[0]
max_results = 10000

# get the tweets by hashtag and save them to a file
with open (hashtag + ".tsv", 'w', encoding='utf-8', newline='') as f:
    #writer = csv.writer(f, delimiter='\t')
    f.write("text\tusername\tfollowers\n")
    for i,tweet in enumerate(sntwitter.TwitterHashtagScraper(hashtag).get_items()):
        if i > max_results:
            break
        else:
            text = tweet.content.replace('\n', ' ')
            f.write(text + "\t" + tweet.user.username + "\t" + str(tweet.user.followersCount) + "\n")