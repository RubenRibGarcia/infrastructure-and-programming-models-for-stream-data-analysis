#!/usr/bin/env python3

import os, sys, subprocess, shlex
import logging, json
import inquirer

logging.basicConfig(format="%(levelname) -10s %(asctime)s %(funcName)s:%(lineno)s: %(message)s")

SPDS_INFRASTRUCTURE='/home/rgarcia/workspace/ISEL/dissertacao/infrastructure-and-programming-models-for-stream-data-analysis/spds-infrastructure'
GOOGLE_APPLICATION_CREDENTIALS= SPDS_INFRASTRUCTURE + '/envs/gcp-thesis-service-account.json'

def main():
    TERRAFORM_BASE = "{}/terraform/gcp".format(SPDS_INFRASTRUCTURE)

    try:

        # https://security.openstack.org/guidelines/dg_avoid-shell-true.html
        terraformOutput = subprocess.check_output(shlex.split("terraform output -json"),
                    shell=False, cwd="{}".format(TERRAFORM_BASE)).decode('utf-8')

        terraformData = json.loads(terraformOutput)

        instances_names = terraformData['instances_names']['value']
        instances_public_ips = terraformData['instances_public_ips']['value']

        services = list(map(lambda x: x, instances_names))
        
        connect = dict()
        for serv in services:
            if len(instances_names[serv]) > 0:
                index = 0
                while index < len(instances_names[serv]):
                    connect[instances_names[serv][index]] = instances_public_ips[serv][index]
                    index = index + 1
        
    except subprocess.CalledProcessError as ex:
        logging.exception("Error getting terraform output: {}" % ex.output)
    except Exception:
        logging.exception("Exception")

    questions = [
        inquirer.List('serverName', message="Connect to which server? (ssh)", choices=connect.keys())
    ]

    answers = inquirer.prompt(questions)
    

    print("/usr/bin/ssh", "{}".format(connect[answers['serverName']]))
    os.execl("/usr/bin/ssh", "-o ConnectTimeout=5", "{}".format(connect[answers['serverName']]))
    
    return 0


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)