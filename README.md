Todo:

1. build: mvn clean package

2. configure: 
	a. bootstrap server for producer and consumer
	b. kafka topic 
	각 파일에 "fill in"으로 주석 달아놨습니다.

3. run: 
	a. start zookeeper server
	b. start kafka server
	c. run producer: java -cp target/dj-pipeline.jar com.pipeline.comments.YouTubeCommentCrawlerWithKafka (Youtube comment crawler)
	(c`) check topic contents with kafka console consumer
	d. run prection: python sentimentAnalysis/sentimental_analysis.py (KoBERT sentimental classification)

4. requirements:
	CUDA 11.4, torch 1.11.0, python 3.8, Driver Version: 470.182.03
	gluonnlp==0.8.0 kobert-tokenizer==0.1 mxnet==1.9.1 numpy==1.22.4 pandas==2.0.3 PyYAML==6.0.1 tqdm==4.66.1 transformers==4.34.0 urllib3==2.0.6
