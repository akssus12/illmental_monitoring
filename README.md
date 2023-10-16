Todo:

1. build: mvn clean package

2. configure: 
	a. bootstrap server for producer and consumer
	b. kafka topic 
	각 파일에 "fill in"으로 주석 달아놨습니다.

3. run: 
	a. start zookeeper server
	b. start kafka server
	c. run producer: java -cp target/dj-pipeline.jar com.pipeline.comments. YouTubeCommentCrawlerWithKafka
	(c`) check topic contents with kafka console consumer
	d. run producer: python analysis.py at sentimentAnalysis/
