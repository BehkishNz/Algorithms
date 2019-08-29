import re
import tweepy
from tweepy import OAuthHandler
from textblob import TextBlob
import datetime
import os
import numpy as np
import pandas as pd
import pickle
import quandl
import plotly.offline as py
import plotly.graph_objs as go
import plotly.figure_factory as ff



class TwitterClient(object):
    '''
    Generic Twitter Class for sentiment analysis.
    '''

    def __init__(self):
        '''
        Class constructor or initialization method.
        '''
        # keys and tokens from the Twitter Dev Console
        consumer_key = 'VUldJxV5KsLRESYpnIO6rxdMu'
        consumer_secret = '7qCOFkvOOAVQm3t2rmNRMAvmfiGID8kq5DeSCVkIO2EP5w6Jyv'
        access_token = '1352992446-u3W9dIPnUA3lKojaoqhKmpAKZktPbsJTvAjKZzX'
        access_token_secret = 'bw2B8WcZh2Tz1GyDrePxXm2Pepm2zokD5n6e1gI5sSaps'

        # attempt authentication
        try:
            # create OAuthHandler object
            self.auth = OAuthHandler(consumer_key, consumer_secret)
            # set access token and secret
            self.auth.set_access_token(access_token, access_token_secret)
            # create tweepy API object to fetch tweets
            self.api = tweepy.API(self.auth)
        except:
            print("Error: Authentication Failed")

    def clean_tweet(self, tweet):
        return ' '.join(re.sub("(@[A-Za-z0-9]+)|([^0-9A-Za-z \t])| (\w+:\ / \ / \S+)", " ", tweet).split())

    def get_tweet_sentiment(self, tweet):

        analysis = TextBlob(self.clean_tweet(tweet))
        if analysis.sentiment.polarity >= 0.5:
            return 'very positive'
        elif(analysis.sentiment.polarity > 0) :
            return 'positive'
        elif analysis.sentiment.polarity == 0:
            return 'neutral'
        elif analysis.sentiment.polarity <= - 0.5:
            return 'very negative'
        else:
            return 'negative'

    def get_tweet_sentiment_score(self, tweet):

        analysis = TextBlob(self.clean_tweet(tweet))
        return analysis.sentiment.polarity >= 0.5



    def get_tweets(self, query, count=10, until = datetime.date.fromordinal(datetime.date.today().toordinal()+1).strftime("%Y-%m-%d")):
        tweets = []

        try:
            # call twitter api to fetch tweets
            fetched_tweets = self.api.search(q=query, count=count, until = until)

            # parsing tweets one by one
            for tweet in fetched_tweets:
                # empty dictionary to store required params of a tweet
                parsed_tweet = {}

                # saving text of tweet
                parsed_tweet['text'] = tweet.text
                # saving sentiment of tweet
                parsed_tweet['sentiment'] = self.get_tweet_sentiment(tweet.text)
                parsed_tweet['created_at'] = tweet.created_at
                parsed_tweet['polarity'] = self.get_tweet_sentiment_score(tweet.text)

                # appending parsed tweet to tweets list
                if tweet.retweet_count > 0:
                    # if tweet has retweets, ensure that it is appended only once
                    if parsed_tweet not in tweets:
                        tweets.append(parsed_tweet)
                else:
                    tweets.append(parsed_tweet)

                    # return parsed tweets
            return tweets


        except tweepy.TweepError as e:
            # print error (if any)
            print("Error : " + str(e))


def get_quandl_data(quandl_id):
    cache_path = '{}.pkl'.format(quandl_id).replace('/','-')
    try:
        f = open(cache_path, 'rb')
        df = pickle.load(f)
        print('Loaded {} from cache'.format(quandl_id))
    except (OSError, IOError) as e:
        print('Downloading {} from Quandl'.format(quandl_id))
        df = quandl.get(quandl_id, returns="pandas")
        df.to_pickle(cache_path)
        print('Cached {} at {}'.format(quandl_id, cache_path))
    return df


def main():
    # creating object of TwitterClient Class

    api = TwitterClient()
    # calling function to get tweets
    i = 10
    btc_usd_price_kraken = get_quandl_data('BCHARTS/KRAKENUSD')
    maxday = i
    bitcoinInfo = btc_usd_price_kraken.tail(maxday)
    while i > 0:
        i = i - 1
        searchdate = datetime.date.fromordinal(datetime.date.today().toordinal()-i + 1).strftime("%Y-%m-%d")
        printsearchdate = datetime.date.fromordinal(datetime.date.today().toordinal() - i ).strftime("%Y-%m-%d")
        print("on " + printsearchdate +", the price was $" + str(bitcoinInfo['Weighted Price'][maxday-(i+1)]) )
        tweets = api.get_tweets(query='#Bitcoin', count=100, until =searchdate )

        # picking positive tweets from tweets
        ptweets = [tweet for tweet in tweets if tweet['sentiment'] == 'positive']
        vptweets = [tweet for tweet in tweets if tweet['sentiment'] == 'very positive']
        # percentage of positive tweets
        print("Very Positive tweets percentage: {} %".format(100 * len(vptweets) / len(tweets)))
        print("Positive tweets percentage: {} %".format(100 * len(ptweets) / len(tweets)))
        # picking negative tweets from tweets
        ntweets = [tweet for tweet in tweets if tweet['sentiment'] == 'negative']
        vntweets = [tweet for tweet in tweets if tweet['sentiment'] == 'very negative']
        # percentage of negative tweets
        print("Negative tweets percentage: {} %".format(100 * len(ntweets) / len(tweets)))
        print("Very Negative tweets percentage: {} %".format(100 * len(vntweets) / len(tweets)))
        # percentage of neutral tweets
        nutweets = [tweet for tweet in tweets if tweet['sentiment'] == 'neutral']
        print("Neutral tweets percentage: {} %" .format(100 * len(nutweets) / len(tweets)))
        counter = 0
        # # print("\n\nNegative tweets:")
        total_polarity = 0;
        for tweet in tweets:
            counter = counter + 1
            total_polarity = total_polarity + float(tweet['polarity']);
            #print(str(counter) + ". " + str(tweet))
        print("average polarity = " + str(total_polarity/len(tweets)))
        print("----------------------------------------------")

        # # printing first 5 positive tweets
        # counter = 0
        # #print("\n\nPositive tweets:")
        # for tweet in ptweets:
        #     counter = counter + 1
        #     print(str(counter) + ". " + str(tweet))
        #
        # # printing first 5 negative tweets


print("started")
#score = TextBlob('bitcoin is going way way down').sentiment.polarity
#print(score)
main()