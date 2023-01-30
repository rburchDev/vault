setup-mongo:
	helm install mongo -f k8s/helm/mongo/values.yaml k8s/helm/mongo --create-namespace --namespace vault

setup-vault:
	helm install vault -f k8s/helm/vault/values.yaml k8s/helm/vault --namespace vault

remove-mongo:
	helm delete mongo --namespace vault

package:
	mvn package

run-package:
	java -jar target/spring-vault-0.0.1-SNAPSHOT.jar

docker-build:
	docker build -t rburch4/vaultapp:1.0.0 -f docker/springboot/Dockerfile .
	docker push rburch4/vaultapp:1.0.0

build:
	helm delete vault -n vault
	make package
	make docker-build
	make setup-vault