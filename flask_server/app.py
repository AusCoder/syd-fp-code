from flask import Flask
from flask import request
from flask import make_response
import time
import json
import pika

# some obscure error message
ERROR_MESSAGE = '{"message":"bad timing!"}'

# define a simple flask server
app = Flask(__name__)

@app.route("/compute-fibonaccis", methods=["POST"])
def hello_world():
    # sometimes our server responds with an error
    # x = int(time.time())
    # if (x % 2 == 0):
    #     resp = make_response(ERROR_MESSAGE, 500)
    #     resp.headers['Content-Type'] = 'application/json'
    #     return resp

    # connect to rabbitmq on localhost
    # make a new conncetion each time to avoid timeout troubles with pika
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='task_queue')
    content = request.get_json()
    channel.basic_publish(exchange='',
                          routing_key='task_queue',
                          body=json.dumps(content))
    connection.close()

    return 'ok'


# maybe put a get method here for use with an id
