#!/usr/bin/env python3

import redis
import os
import csv
import json
import argparse

def main(args):
    r = redis.Redis(host=args.redis_host, port=6379)

    for filename in os.listdir(args.data_path):
        if filename.endswith('.csv') and filename.startswith('ipma'):
            print(f"Migrating {filename} to Redis")
            data_name = filename.replace('.csv', '').replace('ipma_', '').replace('_2018', '')
            print(data_name)
            with open(os.path.join(args.data_path, filename), 'r') as csv_file:
                records = csv.DictReader(csv_file)
                for record in records:
                    key = __compose_ipma_key(record)
                    map = __compose_ipma_values_to_map(record)
                    r.hset(data_name, key, json.dumps(map))

    print("Finished migrating all IPMA data to Redis")


def __compose_ipma_key(record):
    return str(record['MS']) + str(record['DI']) + str(record['HR'])


def __compose_ipma_values_to_map(record):
    return {'1200535': record['1200535'], '1200579': record['1200579'], '1210762': record['1210762']}


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('-rh --redis-host', action='store', required=True, dest='redis_host')
    parser.add_argument('-dp --data-path', action='store', required=True, dest='data_path')

    args = parser.parse_args()

    main(args)
