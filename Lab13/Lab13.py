import boto3
import json

sns_client = boto3.client('sns')
sqs_client = boto3.client('sqs')

topic_response = sns_client.create_topic(Name='BaysaTopic')
topic_arn = topic_response['TopicArn']

queue1_response = sqs_client.create_queue(QueueName='BaysaQ1')
queue2_response = sqs_client.create_queue(QueueName='BaysaQ2')

queue1_url = queue1_response['QueueUrl']
queue2_url = queue2_response['QueueUrl']
queue1_arn = sqs_client.get_queue_attributes(QueueUrl=queue1_url, AttributeNames=['QueueArn'])['Attributes']['QueueArn']
queue2_arn = sqs_client.get_queue_attributes(QueueUrl=queue2_url, AttributeNames=['QueueArn'])['Attributes']['QueueArn']

sns_client.subscribe(TopicArn=topic_arn, Protocol='sqs', Endpoint=queue1_arn)
sns_client.subscribe(TopicArn=topic_arn, Protocol='sqs', Endpoint=queue2_arn)

def publish_message(message):
    print(f"Publishing: {message}")
    sns_client.publish(TopicArn=topic_arn, Message=message)

def consume_message_1():
    response = sqs_client.receive_message(QueueUrl=queue1_url, MaxNumberOfMessages=1)
    if 'Messages' in response and len(response['Messages']) > 0:
        message = response['Messages'][0]
        body = message['Body']
        if 'Message' in message['Body'] and type(body)==str:
            body = json.loads(message['Body'])['Message']
        print(f"Consumer 1: {body}")
        sqs_client.delete_message(QueueUrl=queue1_url, ReceiptHandle=message['ReceiptHandle'])

def consume_message_2():
    response = sqs_client.receive_message(QueueUrl=queue2_url, MaxNumberOfMessages=1)
    if 'Messages' in response and len(response['Messages']) > 0:
        message = response['Messages'][0]
        body = message['Body']
        if 'Message' in message['Body'] and type(body)==str:
            body = json.loads(message['Body'])['Message']
        print(f"Consumer 2: {body}")
        sqs_client.delete_message(QueueUrl=queue2_url, ReceiptHandle=message['ReceiptHandle'])

publish_message("hello")

consume_message_1()
consume_message_2()