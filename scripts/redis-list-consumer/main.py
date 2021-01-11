#!/usr/bin/env python3

import redis
import argparse


def main(args):
    r = redis.Redis(host=args.redis_host, port=6379)

    while True:
        list_size = r.llen(args.redis_key)
        print(f"Removing {list_size} elements from {args.redis_key}")
        for i in range(0, r.llen(args.redis_key)):
            r.rpop(args.redis_key)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-rh --redis-host', action='store', required=True, dest='redis_host')
    parser.add_argument('-rk --redis-key', action='store', required=True, dest='redis_key')

    args = parser.parse_args()

    main(args)
