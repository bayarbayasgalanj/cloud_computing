package cs544.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class SQSMessageListener {

    @Value("${aws.region}")
    private String awsRegion;
    private final Region region = Region.of(awsRegion);
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;
    private SqsClient sqsClient;
    private static String queueUrl;

    @PostConstruct
    private void init() {
        SnsClient snsClient = SnsClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        sqsClient = SqsClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        // Create an SNS topic
        CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                .name("LiveScore")
                .build();
        CreateTopicResponse createTopicResponse = snsClient.createTopic(createTopicRequest);
        String topicArn = createTopicResponse.topicArn();

        // Create two SQS queues
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("Score")
                .build();
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        queueUrl = createQueueResponse.queueUrl();

        // Get the ARNs of the queues
        String queueArn = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                        .queueUrl(queueUrl)
                        .attributeNames(QueueAttributeName.QUEUE_ARN)
                        .build())
                .attributes()
                .get(QueueAttributeName.QUEUE_ARN);

        // Subscribe the queues to the SNS topic
        SubscribeRequest subscribeRequest1 = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sqs")
                .endpoint(queueArn)
                .build();
        snsClient.subscribe(subscribeRequest1);

        // Set the SQS queue attributes to receive messages from SNS
        Map<QueueAttributeName, String> queueAttributes = new HashMap<>();
        queueAttributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
        queueAttributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, "300");

        SetQueueAttributesRequest setQueueAttributesRequest = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(queueAttributes)
                .build();
        sqsClient.setQueueAttributes(setQueueAttributesRequest);
    }

    public void receiveAndProcessMessages() {

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .build();

        while (true) {
            ReceiveMessageResponse receiveResponse = sqsClient.receiveMessage(receiveRequest);
            for (Message message : receiveResponse.messages()) {
                System.out.println("Message: " + message.body());
                deleteMessage(queueUrl, message.receiptHandle());
            }
        }
    }

    private static void deleteMessage(String queueUrl, String receiptHandle) {
        SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_1).build();

        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsClient.deleteMessage(deleteRequest);
    }
}
