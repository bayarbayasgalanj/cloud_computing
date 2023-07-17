import boto3

def lambda_handler(event, context):
    sns = boto3.client('sns')

    message = event.get('message', 'Default message')

    params = {
        'Message': message,
        'TopicArn': 'arn:aws:sns:us-east-1:896553604990:LiveScore'
    }

    try:
        response = sns.publish(**params)
        message_id = response['MessageId']
        print('Message published:', message_id)
        return response
    except Exception as e:
        print('Error publishing message:', str(e))
        raise e
