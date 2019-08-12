# JAQU-CAZ Template for Lambda using Spring Boot

## Prerequisites

* Java 8
* aws-cli (for deployment).
See official [AWS Guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
for instructions.
* aws-sam-cli (for testing locally). See official [AWS Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
for instructions.
* Docker (for testing using mocks from Localstack and for aws-sam)

## Developer's setup

There are style guides for _Eclipse_ and _Intellij IDEA_ located in `developer-resources`.

## (dev) Deployment

The following command will build, pack and deploy the service as a artifact used by AWS Lambda
and API Gateway.

```
$ make build deploy-to-aws S3_BUCKET_NAME=name_of_your_bucket STACK_NAME=name_of_your_stack
```

To only deploy:

```
$ make deploy-to-aws S3_BUCKET_NAME=name_of_your_bucket STACK_NAME=name_of_your_stack
```

## Local Development: building, running and testing

[Detailed descripton of how to build, run and test Tariff service](RUNNING_AND_TESTING.md)

## API specification

API specification is available at `{server.host}:{server.port}/v2/api-docs` (locally usually at http://localhost:8080/v2/api-docs)

## Database management

Liquibase is being used as database migrations tool.
Please check `src/main/resources/db.changelog` directory. It contains file named `db.changelog-master.yaml`
which is automatically picked up by Spring Boot at application startup. This file drives
application of all changesets and migrations.

### Liquibase naming convention
Each changeset should be prefixed with consecutive 4-digit number left padded with zeros.
For example: 0001, 0002, 0003. Then current application version should be put and finally some
short description of change. For example:

`0001-1.0-create_tables_taxi_phv_licensing_authority.yaml`

What we see is application order number, at what application version change was made and finally
a short description of changes. Pretty informative and clean.

If one changeset file contains more than one change, please put consecutive numbers in changeset id:

`0002.1`, `0002.2` and so on.

Raw SQL files must be used from Liquibase Yaml changesets and put into `rawSql` subfolder.
Please use existing files as an example.