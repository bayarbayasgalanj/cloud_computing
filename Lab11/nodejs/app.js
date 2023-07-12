const port = process.env.PORT || 3000,
    http = require('http'),
    fs = require('fs'),
    html = fs.readFileSync('index.html');

const log = function(entry) {
    fs.appendFileSync('/tmp/sample-app.log', new Date().toISOString() + ' - ' + entry + '\n');
};

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-east-1'});

var ddb = new AWS.DynamoDB({apiVersion: '2012-08-10'});
var docClient = new AWS.DynamoDB.DocumentClient({apiVersion: '2012-08-10'});

const paramsTable = {
    TableName: 'Counter',
    AttributeDefinitions: [
        {
            AttributeName: 'ID',
            AttributeType: 'S'
        }
    ],
    KeySchema: [
        {
            AttributeName: 'ID',
            KeyType: 'HASH'
        },
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1
    },
    StreamSpecification: {
        StreamEnabled: false
    }
};

// ddb.createTable(paramsTable, function(err, data) {
//     if (err) {
//         console.log("Error", err);
//     } else {
//         console.log("Table Created", data);
//     }
// });

const paramsItem = {
    TableName: 'Counter',
    Item: {
        'ID': {S: 'requestCounter'},
        'info': {M: {'callCounter': {N: '1'}}}
    }
};

ddb.putItem(paramsItem, function(err, data) {
    if (err) {
        console.log("Error", err);
    } else {
        console.log("Success", data);
    }
});

const incrementCounter = function() {
    const paramsUpdate = {
        TableName: 'Counter',
        Key: {
          'ID': 'requestCounter'
        },
        UpdateExpression: 'ADD info.callCounter :r',
        ExpressionAttributeValues: {
          ':r': 1
        }
    };
    
    docClient.update(paramsUpdate, function(err, data) {
      if (err) {
        console.log('Error:', err);
      } else {
        console.log('Success: UPDATE ', data);
      }
    });
  };
    
  const getCounterValue = () => {
    const paramsGet = {
      TableName: 'Counter',
      Key: {
        'ID': 'requestCounter'
      }
    };
  
    return new Promise((resolve, reject) => {
      docClient.get(paramsGet, (error, data) => {
        if (error) {
          reject(error);
        } else {
          resolve(data.Item ? data.Item.info.callCounter : 0);
        }
      });
    });
  };
  
  
const server = http.createServer(function (req, res) {
    if (req.method === 'POST') {
        let body = '';

        req.on('data', function(chunk) {
            body += chunk;
        });

        req.on('end', function() {
            if (req.url === '/') {
                log('Received message: ' + body);
            } else if (req.url === '/scheduled') {
                log('Received task ' + req.headers['x-aws-sqsd-taskname'] + ' scheduled at ' + req.headers['x-aws-sqsd-scheduled-at']);
            }

            res.writeHead(200, 'OK', {'Content-Type': 'text/plain'});
            res.end();
        });
    } else {
        incrementCounter(); // Increment the value counter
        getCounterValue()
        .then((counterValue) => {
        // Prepare the HTML response
        const htmlResponse = html.toString().replace('{{counter}}', counterValue);

        // Send the HTML response to the requester
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.write(htmlResponse);
        res.end();
        })
        .catch((error) => {
        console.log('Error:', error);
        res.writeHead(500);
        res.end('Internal Server Error');
        });
    }
});

server.listen(port, function () {
    console.log('Server running at http://127.0.0.1:' + port + '/');
});
