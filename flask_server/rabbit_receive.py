import pika
import json
import requests

# a slow asynchronous computation
def fibonaccis_slow(x):
    results = []
    if (x < 0):
        return results
    else:
        n = 0
        last_fib = fib(n)
        while (last_fib <= x):
            results.append(last_fib)
            n += 1
            last_fib = fib(n)
        return results

def fib(n):
    if (n == 0):
        return 0
    elif (n == 1):
        return 1
    else:
        return fib(n-1) + fib(n-2)

def fibonaccis_fast(x):
    result = [0]
    a = 0
    b = 1
    while (b <= x):
        result.append(b)
        c = b
        b = a + b
        a = c
    return result

# define the callback to be run on each message
def callback(ch, method, properties, body):
    content = json.loads(body)
    results = compute(content['value'])
    to_send = {
        'id': content['id'],
        'results': results
    }
    # results_json = json.dumps(result_dict)
    send_results(content['webhookUrl'], to_send)

# function that computes the results
def compute(value):
    return fibonaccis_slow(value)

# function that sends the http results
def send_results(webhook_url, to_send):
    print(webhook_url)
    print(to_send)

    try:
        headers = {'content-type': 'application/json'}
        requests.post(webhook_url, json = to_send, headers = headers)
        print(" [x] sent results %s to url %s" % (str(to_send['results']), webhook_url))
    except Exception as e:
        print(" [x] failed to send results to %s" % webhook_url)
        print(e)

def main():
    # connect to the rabbitmq queue
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='task_queue')

    # consume messages from the queue
    channel.basic_consume(callback,
                          queue='task_queue',
                          no_ack=True)
    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

if __name__ == "__main__":
    main()
    # print(fibonaccis_fast(500))
