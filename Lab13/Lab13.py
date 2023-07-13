import boto3

# Create the AWS clients for SNS and SQS
sns_client = boto3.client('sns')
sqs_client = boto3.client('sqs')

# Create the SNS topic
topic_response = sns_client.create_topic(Name='MyTopic')
topic_arn = topic_response['TopicArn']

# Create the SQS queues
queue1_response = sqs_client.create_queue(QueueName='Queue1')
queue2_response = sqs_client.create_queue(QueueName='Queue2')

# Retrieve the queue ARNs
queue1_arn = sqs_client.get_queue_attributes(QueueUrl=queue1_response['QueueUrl'], AttributeNames=['QueueArn'])['Attributes']['QueueArn']
queue2_arn = sqs_client.get_queue_attributes(QueueUrl=queue2_response['QueueUrl'], AttributeNames=['QueueArn'])['Attributes']['QueueArn']

# Subscribe the queues to the topic
sns_client.subscribe(TopicArn=topic_arn, Protocol='sqs', Endpoint=queue1_arn)
sns_client.subscribe(TopicArn=topic_arn, Protocol='sqs', Endpoint=queue2_arn)

# Implement the publisher
def publish_message(message):
    sns_client.publish(TopicArn=topic_arn, Message=message)

# Implement the first consumer
def consume_message_1():
    response = sqs_client.receive_message(QueueUrl=queue1_response['QueueUrl'], MaxNumberOfMessages=1)
    print (response)
    if 'Messages' in response:
        message = response['Messages'][0]
        body = message['Body']
        print(f"Consumer 1: {body}")
        sqs_client.delete_message(QueueUrl=queue1_response['QueueUrl'], ReceiptHandle=message['ReceiptHandle'])

# Implement the second consumer
def consume_message_2():
    response = sqs_client.receive_message(QueueUrl=queue2_response['QueueUrl'], MaxNumberOfMessages=1)
    if 'Messages' in response:
        message = response['Messages'][0]
        body = message['Body']
        print(f"Consumer 2: {body}")
        sqs_client.delete_message(QueueUrl=queue2_response['QueueUrl'], ReceiptHandle=message['ReceiptHandle'])

# Publish a message to the topic
publish_message("hello")

# Consume messages from the queues
consume_message_1()
consume_message_2()
