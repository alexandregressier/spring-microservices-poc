#!/usr/bin/env make -f

GRADLE = ./gradlew

NS_EFK = efk
NS_POSTGRESQL = default
NS_KEYCLOAK = default
NS_KAFKA = default
NS_REDIS = default
NS_OSP = default


.PHONY: deploy
deploy: deploy/postgresql deploy/keycloak deploy/kafka deploy/redis deploy/osp

.PHONY: deploy/efk
deploy/efk:
	skaffold run -f deployment/vendor/efk/skaffold.yaml
	kubectl apply -f deployment/vendor/efk/fluent-bit.yaml

.PHONY: deploy/postgresql
deploy/postgresql:
	kubectl apply -f deployment/vendor/postgresql/postgresql-init.yaml
	skaffold run -f deployment/vendor/postgresql/skaffold.yaml

.PHONY: deploy/keycloak
deploy/keycloak:
	kubectl apply -f deployment/vendor/keycloak/keycloak.yaml

.PHONY: deploy/kafka
deploy/kafka:
	skaffold run -f deployment/vendor/kafka/skaffold.yaml

.PHONY: deploy/redis
deploy/redis:
	skaffold run -f deployment/vendor/redis/skaffold.yaml

.PHONY: deploy/osp
deploy/osp: deploy/meta-services deploy/services
.PHONY: deploy/meta-services
deploy/meta-services: ; skaffold run -f deployment/meta-services/skaffold.yaml
.PHONY: deploy/services
deploy/services: ; skaffold run -f deployment/services/skaffold.yaml


.PHONY: pf
pf: pf/postgresql pf/kafka pf/redis pf/eureka pf/gateway

.PHONY: pf/kibana
pf/kibana:
	kubectl -n $(NS_EFK) port-forward service/kibana-kibana 5601:http

.PHONY: pf/postgresql
pf/postgresql:
	kubectl -n $(NS_POSTGRESQL) port-forward service/postgresql 5432:tcp-postgresql

.PHONY: pf/keycloak
pf/keycloak:
	kubectl -n $(NS_KEYCLOAK) port-forward service/keycloak 80:http

.PHONY: pf/kafka
pf/kafka:
	kubectl -n $(NS_KAFKA) port-forward service/kafka 9092:tcp-client

.PHONY: pf/redis
pf/redis: pf/redis-master pf/redis-slave
.PHONY: pf/redis-master
pf/redis-master: ; kubectl -n $(NS_REDIS) port-forward service/redis-master 6379:redis
.PHONY: pf/redis-slave
pf/redis-slave: ; kubectl -n $(NS_REDIS) port-forward service/redis-slave 6380:redis

.PHONY: pf/eureka
pf/eureka:
	kubectl -n $(NS_OSP) port-forward service/osp-eureka-service 8761:http

.PHONY: pf/gateway
pf/gateway:
	kubectl -n $(NS_OSP) port-forward service/osp-gateway-service 8080:http


.PHONY: build
build:
	$(GRADLE) build

.PHONY: buildImage
buildImage:
	$(GRADLE) bootBuildImage

.PHONY: clean
clean:
	$(GRADLE) clean