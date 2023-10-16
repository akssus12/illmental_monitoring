package com.pipeline.comments;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.IOException;

import java.security.GeneralSecurityException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class YouTubeCommentCrawlerWithKafka {
    // You need to set this value for your code to compile.
    // For example: ... DEVELOPER_KEY = "YOUR ACTUAL KEY";
    private static final String DEVELOPER_KEY = "AIzaSyAMai6p0E_vfA3MsHOXgnmFL7h8k1TMDN0";
    private static final String APPLICATION_NAME = "ytbCommentCrawler";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String KAFKA_BOOTSTRAP_SERVERS = "YOUR_KAFKA_BROKER"; //fill in
    private static final String KAFKA_TOPIC = "YOUR_TOPIC_NAME"; // fill in
 
	private static final String URLfile = "URLs.txt"; 
/**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Send comments to Kafka topic.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
		
		List<String> videoIds = getVideoIds(URLfile);
		
		for (String videoId : videoIds) {

			String nextPageToken = null;

	        // Configure Kafka producer
   	    	Properties producerProps = new Properties();
   	    	producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
   	    	producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
   	    	producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        	try (Producer<String, String> producer = new KafkaProducer<>(producerProps)) {
				while(true){

					YouTube.CommentThreads.List request = youtubeService.commentThreads()
                    	.list(Arrays.asList("snippet"));
                	CommentThreadListResponse response = request.setKey(DEVELOPER_KEY)
                    	.setMaxResults(100L)
                    	.setVideoId(videoId)
                    	.setPageToken(nextPageToken)
                    	.execute();

            		// Iterate through the comment threads and send comments to the Kafka topic
	            	for (CommentThread commentThread : response.getItems()) {
	                	String commentText = commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay();
	                	ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, commentText);
	                	producer.send(record);
	            	}

					// Check if there are more pages of results
                	nextPageToken = response.getNextPageToken();
                	if (nextPageToken == null) {
                    	break; // No more pages
                	}
				}
			} catch (Exception e) {
	            	e.printStackTrace();
	    	}
    		System.out.println("Comments from commentThreads have been sent to the Kafka topic: " + KAFKA_TOPIC);
    	}
	}

	private static List<String> getVideoIds(String filePath) {
		List <String> videoIds = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
			String line;
			while((line = br.readLine()) != null) {
				String videoId = extractVideoId(line);
				if (videoId != null) {
					videoIds.add(videoId);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return videoIds;
	}
    private static String extractVideoId(String videoUrl) {
        // Extract the video ID from the YouTube video URL
        String videoId = null;
        String[] urlParts = videoUrl.split("v=");
        if (urlParts.length > 1) {
            videoId = urlParts[1];
        }
        return videoId;
    }
}

