#!/usr/bin/env python3
import time

import redis
import argparse
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)-8s %(message)s')


def main(args):
    r = redis.Redis(host=args.redis_host, port=6379)

    while True:
        list_size = r.llen(args.redis_key)

        logging.info(f"Removing {list_size} elements from {args.redis_key}")
        for i in range(0, r.llen(args.redis_key)):
            r.ltrim(args.redis_key, -1, 0)
        time.sleep(5)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-rh --redis-host', action='store', required=True, dest='redis_host')
    parser.add_argument('-rk --redis-key', action='store', required=True, dest='redis_key')

    args = parser.parse_args()

    main(args)
