#!/usr/bin/env python3

import os, sys, subprocess, shlex
import logging, json
import inquirer
import argparse

logging.basicConfig(format="%(message)s", level=logging.INFO)

SPDS = {
    'FLINK': 'flink',
    'STORM': 'storm',
    'KAFKA': 'kafka',
    'ALL': 'all'
}

CLOUD_PROVIDERS = {
    'AWS': 'aws'
}


def main(args):
    try:
        provider = CLOUD_PROVIDERS[args.cloud_provider]
        spds = SPDS[args.spds]

        connect = dict()
        TERRAFORM_BASE = f"{os.getenv('SPDS_INFRASTRUCTURE_PATH')}/terraform/{provider}/{spds}"

        logging.info(f"Calling Terraform output for {provider} at {TERRAFORM_BASE}")
        # https://security.openstack.org/guidelines/dg_avoid-shell-true.html
        terraformOutput = subprocess.check_output(shlex.split("terraform output -json"),
                                                  shell=False, cwd="{}".format(TERRAFORM_BASE),
                                                  stderr=subprocess.DEVNULL).decode('utf-8')

        terraformData = json.loads(terraformOutput)

        if len(terraformData.keys()):
            instances_names = terraformData['instances_names']['value']
            instances_public_ips = terraformData['instances_public_ips']['value']

            services = list(map(lambda x: x, instances_names))

            for serv in services:
                if len(instances_names[serv]) > 0:
                    index = 0
                    while index < len(instances_names[serv]):
                        connect[instances_names[serv][index]] = instances_public_ips[serv][index]
                        index = index + 1

        if len(connect.keys()):
            questions = [
                inquirer.List('serverName', message="Connect to which server? (ssh)", choices=connect.keys())
            ]

            answers = inquirer.prompt(questions)

            if answers is not None:
                print("/usr/bin/ssh", "admin@{}".format(connect[answers['serverName']]))
                os.execl("/usr/bin/ssh", "-o ConnectTimeout=5", "admin@{}".format(connect[answers['serverName']]))
        else:
            logging.info("Exiting because of no instances available to choose")

        return 0

    except subprocess.CalledProcessError as ex:
        logging.exception(f"Error getting terraform output: {ex.output}")
        pass
    except Exception as ex:
        logging.exception(f"{str(ex)}")
        pass


if __name__ == "__main__":
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument('--cloud-provider', action='store', required=True, dest='cloud_provider',
                            choices=list(CLOUD_PROVIDERS.keys()))
        parser.add_argument('--spds', action='store', required=True, dest='spds', choices=list(SPDS.keys()))

        main(parser.parse_args())
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
