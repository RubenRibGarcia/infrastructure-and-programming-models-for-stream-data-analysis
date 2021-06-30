# *SPDS Infrastructure*

### Requirements
* Docker
* Docker-compose
#### For Remote
* Python == 3.9.5
  * Libs:
    * boto3
* Ansible == 2.10.5
* Terraform == 0.14.6

### How to run (Locally)

At `spds-infrastructure/components` folder can be found the folders with all
the system that can be executed locally. To do so:

1. Depending on the SPDS you want to run, first copy the corresponding 
   `docker-compose.yml.example` and fill the volumnes directory that has the prefix `path/to` (or similar);
2. Do the same for `misc-infrastructure` and `metrics-monitor`;

### How to run (Amazon Web Services)

This section assumes that you are running at the base path of this repo,
which is `infrastructure-and-programming-models-for-stream-data-analysis`

1. Before configuring environment variables we must create an Access User
   at AWS with access to S3 and EC2 services.
2. Copy `aws.env.example` from `envs` folder and fill the
corresponding variables.
3. Run `source spds-infrastructure/envs/aws.env` (or the the copied file name)
4. With the help of the Makefile provided at the root folder,
now we can remotely run one of the many SPDS, like `make remote-bootstrap-aws-run-flink`
5. To stop the previous execution you can run `make remote-bootstrap-aws-stop-flink`


### How to run (Google Cloud Plataform)

--Work in progress--

### Metrics (Remote)

While executing one of the SPDS we need to make other configuration, for at least check the
metrics of our systems. To do so we must:

1. Access Grafana Webapp depending the public IP that the metrics-monitor instance
   has been provided;
2. Insert the credential and skip the change of the main password;
3. Configure InfluxBD datasource of the metrics with the following data:
    * URL: http://influxdb:8086
    * Database: telegraf
    * Username: telegraf
    * Username: <configured password for InfluxDB>
4. Import a dashboard from the JSON files provided at `spds-infrastructure/components/metrics-monitor`.
   For example if you started Apache Flink, then you must import `remote_flink_benchmark_dashboard.json`;

   