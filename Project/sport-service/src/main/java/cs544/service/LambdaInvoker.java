package cs544.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Service
public class LambdaInvoker {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;
    @Value("${aws.lambda-function}")
    private String lambdaFunctionName;
    private LambdaClient lambdaClient;
    @PostConstruct
    private void init() {
        // Set up the Lambda client
        lambdaClient = LambdaClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    public void sendMessage(String message){
        // Create an InvokeRequest
        InvokeRequest request = InvokeRequest.builder()
                .functionName(lambdaFunctionName)
                .payload(SdkBytes.fromUtf8String("{\"message\":\" " + message + "\"}"))
                .build();

        try {
            // Invoke the Lambda function
            InvokeResponse response = lambdaClient.invoke(request);

            // Get the response payload
            String payload = new String(response.payload().asByteArray(), StandardCharsets.UTF_8);
            System.out.println("Response Payload: " + payload);
        } catch (LambdaException e) {
            System.err.println("Error invoking Lambda function: " + e.getMessage());
        }
    }
}
