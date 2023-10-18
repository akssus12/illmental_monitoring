from kafka import KafkaConsumer
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer

count=0
# Initialize the sentiment analyzer
analyzer = SentimentIntensityAnalyzer()

# Configure Kafka consumer
consumer = KafkaConsumer(
    'youtube_comment',  # fill in
    bootstrap_servers='localhost:9092',  #fill in
    value_deserializer=lambda x: x.decode('utf-8'),
    auto_offset_reset='earliest'
)

# Consume and analyze comment
try:
    for message in consumer:
        count += 1
        comment_text = message.value
        sentiment_scores = analyzer.polarity_scores(comment_text)
        compound_score = sentiment_scores['compound']
        bert_score = sentiment_pipeline(comment_text)

        # Determine sentiment based on the compound score
        if compound_score >= 0.01:
          sentiment = 'Positive'
        elif compound_score <= -0.01:
            sentiment = 'Negative'
        else:
            sentiment = 'Neutral'

        # Print the sentiment and comment text
        #print(f"count: {count}")
        print(f"Sentiment: {sentiment}")
        print(f"Comment: {comment_text}")
        print(f"score: {compound_score}")
        print(f"BERT score: {bert_score}")


except Exception as e:
    print(f"Error:{e}")