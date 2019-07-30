.PHONY: \
	build \
	build-yolo \
	package \
	integration-test \
	unit-test \
	run \
	generate-javadoc \
	clean \
	deploy-to-aws \
	create-archetype

build:
	./mvnw verify -P jacoco

build-yolo:
	./mvnw verify -DskipTests

package:
	./mvnw package

unit-test:
	./mvnw clean test -P jacoco

integration-test:
	./mvnw verify -DskipUnitTests -P jacoco

run:
	./mvnw spring-boot:run

run-development:
	SPRING_PROFILES_ACTIVE=development ./mvnw spring-boot:run

generate-javadoc:
	./mvnw javadoc:javadoc

clean:
	./mvnw clean

deploy-to-aws:
	test $(S3_BUCKET_NAME)
	test $(STACK_NAME)

	aws cloudformation package \
	--template-file sam.yaml \
	--output-template-file /tmp/output-sam.yaml \
	--s3-bucket $(S3_BUCKET_NAME)

	aws cloudformation deploy \
	--template-file /tmp/output-sam.yaml \
	--stack-name $(STACK_NAME) \
	--capabilities CAPABILITY_IAM

	aws cloudformation describe-stacks --stack-name $(STACK_NAME)

create-archetype: clean
	@if test ! -s ~/.m2/settings.xml; then \
		echo settings.xml does not exist, creating one; \
		echo "<settings></settings>" > ~/.m2/settings.xml; \
	fi
	./mvnw archetype:create-from-project
	./mvnw -f target/generated-sources/archetype/pom.xml install

sonar:
	./mvnw sonar:sonar

local-up:
	docker-compose -f docker/docker-compose.yml -p postgres_docker up -d

local-down:
	docker-compose -f docker/docker-compose.yml -p postgres_docker down

local-db-up:
	docker-compose -f docker/docker-compose.yml -p postgres_docker up -d postgres

local-db-down:
	docker-compose -f docker/docker-compose.yml -p postgres_docker down

# Example run: 'make sam-local-run EVENT=src/test/resources/sample_lambda_events/import_10_taxis.json'
sam-local-run:
	SPRING_PROFILES_ACTIVE='sam-local' sam local invoke JaquCazTariffFunction -t sam.yaml -e $$EVENT --docker-network postgres_docker_default

localstack-up:
	SERVICES='s3,sqs,sns' docker-compose -f docker/docker-compose-localstack.yml -p localstack_docker up -d

localstack-down:
	docker-compose -f docker/docker-compose-localstack.yml -p localstack_docker down

local-services-up: local-db-up localstack-up

local-services-down: local-db-down localstack-down

localstack-run:
	SPRING_PROFILES_ACTIVE='localstack' AWS_PROFILE='localstack' AWS_REGION='eu-west-2' ./mvnw spring-boot:run
