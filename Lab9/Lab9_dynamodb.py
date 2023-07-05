import boto3
from botocore.exceptions import ClientError
from decimal import Decimal
import logging
logger = logging.getLogger(__name__)
class Music:
    def __init__(self, dyn_resource):
        self.dyn_resource = dyn_resource
        self.table = None

    def create_table(self, table_name):
        try:
            self.table = self.dyn_resource.create_table(
                TableName=table_name,
                KeySchema=[
                    {'AttributeName': 'Artist', 'KeyType': 'HASH'},  # Partition key
                    {'AttributeName': 'SongTitle', 'KeyType': 'RANGE'}  # Sort key
                ],
                AttributeDefinitions=[
                    {'AttributeName': 'Artist', 'AttributeType': 'S'},
                    {'AttributeName': 'SongTitle', 'AttributeType': 'S'}
                ],
                ProvisionedThroughput={'ReadCapacityUnits': 5, 'WriteCapacityUnits': 5})
            self.table.wait_until_exists()
        except ClientError as err:
            logger.error(
                "Couldn't create table %s. Here's why: %s: %s", table_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return self.table

    def add_music(self, Artist, SongTitle, rating):
        try:
            self.table.put_item(
                Item={
                    'Artist': Artist,
                    'SongTitle': SongTitle,
                    'info': { 'rating': Decimal(str(rating))}
                })
        except ClientError as err:
            logger.error(
                "Couldn't add music %s to table %s. Here's why: %s: %s",
                Artist, self.table.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise

    def get_music(self, Artist, SongTitle):
        try:
            response = self.table.get_item(Key={'Artist': Artist, 'SongTitle': SongTitle})
        except ClientError as err:
            logger.error(
                "Couldn't get music %s from table %s. Here's why: %s: %s",
                SongTitle, self.table.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Item']
    
    def get_music_all(self):
        try:
            response = self.table.scan()
        except ClientError as err:
            logger.error(
                "Couldn't get music from table %s. Here's why: %s: %s",
                self.table.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Items']
    
    def update_music_rating(self, Artist, SongTitle, rating):
        try:
            response = self.table.update_item(
                Key={'Artist': Artist, 'SongTitle': SongTitle},
                UpdateExpression="set info.rating=:r",
                ExpressionAttributeValues={
                    ':r': Decimal(str(rating))},
                ReturnValues="UPDATED_NEW")
        except ClientError as err:
            logger.error(
                "Couldn't update music %s in table %s. Here's why: %s: %s",
                SongTitle, self.table.name,
                err.response['Error']['Code'], err.response['Error']['Message']
            )
            raise
        else:
            return response['Attributes']
        
    def delete_movie(self, Artist, SongTitle):
        try:
            self.table.delete_item(Key={'Artist': Artist, 'SongTitle': SongTitle})
        except ClientError as err:
            logger.error(
                "Couldn't delete movie %s. Here's why: %s: %s", SongTitle,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise


dynamodb_resource = boto3.resource('dynamodb')
musics = Music(dynamodb_resource)
table_name = 'Music'
try:
    existing_table = dynamodb_resource.Table(table_name)
    existing_table.load()
    table = existing_table
except ClientError as e:
    if e.response['Error']['Code'] == 'ResourceNotFoundException':
        table = musics.create_table(table_name)
    else:
        raise e
musics.table = table
musics.add_music("You and Me", "Edsheeran", 5)
musics.add_music("Sugar", "Maroon5", 3)

logger.error("BAYSA *** GET MUSIC  %s",musics.get_music("Sugar", "Maroon5"))
musics.update_music_rating("Sugar", "Maroon5", 4)
logger.error ('BAYSA ** AFTER UPDATE GET MUSIC ALL %s',musics.get_music_all())
musics.delete_movie("You and Me", "Edsheeran")
logger.error ('BAYSA ** AFTER DELETE GET MUSIC ALL %s',musics.get_music_all())