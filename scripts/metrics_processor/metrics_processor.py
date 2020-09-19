#!/bin/python3
import configparser
import pika
import statsd

channel = None
statsd_client = None


def main(config):
    __setup(config)


def __setup(config):
    _rabbit_mq_config = dict(config.items('rabbitmq'))
    _statsd_config = dict(config.items('statsd'))

    __setup_rabbit_mq(_rabbit_mq_config)
    __setup_statsd(_statsd_config)


def __setup_rabbit_mq(rabbit_mq_config):
    credentials = pika.PlainCredentials(username=rabbit_mq_config['username'], password=rabbit_mq_config['password'],
                                        erase_on_connect=True)
    parameters = pika.ConnectionParameters(host=rabbit_mq_config['host'], port=rabbit_mq_config['port'],
                                           credentials=credentials)

    connection = pika.BlockingConnection(parameters=parameters)

    global channel
    channel = connection.channel()


def __setup_statsd(statsd_config):
    global statsd_client
    statsd_client = statsd.StatsClient(statsd_config['host'], statsd_config['port'])


if __name__ == '__main__':
    config = configparser.ConfigParser()
    config.read_file(open(r'config.ini'))

    main(config)
