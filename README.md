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

## Building the app

This will build and package Lambda application. All checks and tests will be run.
Code coverage will be calculated.
```
$ make build
```

## Running the app

This will run Spring-Boot application using `default` profile using AWS profile specified
in `AWS_PROFILE` env variable and AWS Region specified in `AWS_REGION` env variable.
```
$ make run
```


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

## Local Development: Running and Testing

### Manual testing: Mode/Alternative 1: Integration type run and test
In this mode we will run and test our Lambda code in simulated local Lambda runner. 
It can be achieved with support of AWS SAM Local CLI tool. In short, this tool uses Docker to
provide similar runtime to the one AWS uses to run our Lambdas on production.
Moreover, in this mode we will try to use as much real AWS services as possible. For example we
will connect to real S3 buckets and use real SQS and SNS services.
We must somehow mock RDS/Postgres as the one on AWS is not accessible. For that we use Docker container.

##### Prerequisities
1. Have AWS CLI tool installed. See official [AWS Guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
2. Have AWS SAM Local CLI tool installed. See official [AWS Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
3. Make sure you have properly configured AWS credentials and profile in `~/.aws/credentials`. You must have your profile added with access key id and secret access key.
4. Make sure you have **AWS_PROFILE** and **AWS_REGION** environment variables set and exported (and matching your profile from credentials file). SAM Local will pass those credentials automatically to Docker with Lambda runtime.

##### Running and testing 
1. Run `make local-db-up` to spin up local Postgres Docker container if your service requires Postgres. 
It will automatically set proper network and name.
2. Rebuild and repackage your project: `make package`. You will have to do it every time you make any changes.
3. Run `make sam-local-run EVENT=path_to_json_with_lambda_event` - this uses SAM Local tool to run specified Lambda function 
locally and passing input JSON event into it.
Beware as first run will download Docker images which can take a while. 
4. In case of any changes just repackage as described in point 2 and then rerun as in 3.
5. After you're done, drop local Postgres by `make local-db-down` and you are in a clean state.

### Manual testing: Mode/Alternative 2: Mocked AWS Services, ligthweight local run and test
In this mode we will run our app as regular Spring-Boot application, so without any Lambda 
wrappers and Lambda entrypoints. Spring-Boot controller exposes REST endpoints which we can
use from curl or Postman to test functionality. Localstack will be used as mock for AWS services,
including S3, SQS, SNS and more. 

It is important to create dummy AWS profile in your `~./aws/credentials` file.
```
[localstack]
aws_access_key_id = dummy
aws_secret_access_key = dummy
```
Our run configuration will expect this profile to be set up. Access key and secret key can be set
to `dummy` to make sure we won't use any real AWS services.

##### Prerequisities
1. Have AWS CLI tool installed. See official [AWS Guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
2. `[localstack]` profile in `~/.aws/credentials` file.
3. Docker installed.

##### Running and testing 
1. Run `make local-db-up` to spin up local Postgres Docker container if your services requires Postgres.
2. Run `make localstack-up` to spin up Localstack Docker container with mocked AWS services.
3. If you need Postgres you can combine steps 1 and 2 into one and run: `make local-services-up`
to spin Postgres and Localstack using one command.
4. Check `application-localstack.yml` configuration file, Spring profile `localstack` - it overrides AWS service
endpoints to use localhost. (you don't have to change the defaults if you haven't changed Localstack config)
5. Run app by `make localstack-run`. It will set proper env vars and run Spring-Boot app as usual.
6. You can test endpoints using curl or Postman as usual.
7. After you're done, drop local Postgres and Localstack: `make local-services-down` or 
`make local-db-down` and `make localstack-down` if you want to run 2 separate commands.


##### Preparing S3 data
Some example helper AWS commands to prepare data on S3:
1. `aws --endpoint http://localhost:4572 s3 mb s3://ntr-data` - creates S3 bucket `ntr-data`
2. `aws --endpoint http://localhost:4572 s3 cp ~/Downloads/records-10.csv s3://ntr-data` - 
puts `records-10.csv` file from `~/Downloads` into `ntr-data` bucket.
3. `aws --endpoint-url=http://localhost:4572 s3api put-bucket-acl --bucket ntr-data --acl public-read` - 
makes bucket publicly available.

Then you can use S3Client to get this object programmatically using Java.

### Automated tests: Integration tests

Integration tests are a part of the build process and run once the project has been packaged.
They are located in `src/it` which acts as a test-source directory.

All dependent services are created in Docker containers:
- database using `postgres:11-alpine` image
- AWS using `localstack/localstack` image

See `pom.xml` (`docker-maven-plugin`) for details on docker configuration when running integration tests.

To run just integration tests (without unit), please execute `make integration-test` in the root directory.

### Automated tests: Unit tests

Unit tests are a part of the build process and run once the project has been packaged.
They are located in `src/test` which acts as a test-source directory.

They don't require any external dependencies and are expected to be very fast.

To run just unit tests (without integration), please execute `make unit-test` in the root directory.

## API specification

API specification is available at `{server.host}:{server.port}/v2/api-docs` (locally usually at http://localhost:8080/v2/api-docs)
