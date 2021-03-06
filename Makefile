#!/usr/bin/env make -f

GRADLE = ./gradlew


.PHONY: deploy
deploy: deploy/efk deploy/postgresql deploy/keycloak deploy/kafka deploy/redis deploy/zipkin deploy/osp

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

.PHONY: deploy/zipkin
deploy/zipkin:
	kubectl apply -f deployment/vendor/zipkin/zipkin.yaml

.PHONY: deploy/osp
deploy/osp: deploy/meta-services deploy/services
.PHONY: deploy/meta-services
deploy/meta-services: ; skaffold run -f deployment/meta-services/skaffold.yaml
.PHONY: deploy/services
deploy/services: ; skaffold run -f deployment/services/skaffold.yaml


.PHONY: pf
pf: pf/kibana pf/postgresql pf/keycloak pf/kafka pf/redis pf/zipkin pf/eureka pf/gateway

.PHONY: pf/kibana
pf/kibana:
	kubectl port-forward service/kibana-kibana 5601:http

.PHONY: pf/postgresql
pf/postgresql:
	kubectl port-forward service/postgresql 5432:tcp-postgresql

.PHONY: pf/keycloak
pf/keycloak:
	sudo kubectl port-forward service/keycloak 80:http

.PHONY: pf/kafka
pf/kafka:
	kubectl port-forward service/kafka 9092:tcp-client

.PHONY: pf/redis
pf/redis: pf/redis-master pf/redis-slave
.PHONY: pf/redis-master
pf/redis-master: ; kubectl port-forward service/redis-master 6379:redis
.PHONY: pf/redis-slave
pf/redis-slave: ; kubectl port-forward service/redis-slave 6380:redis

.PHONY: pf/zipkin
pf/zipkin:
	kubectl port-forward service/zipkin 9411:http

.PHONY: pf/eureka
pf/eureka:
	kubectl port-forward service/osp-eureka-service 8761:http

.PHONY: pf/gateway
pf/gateway:
	kubectl port-forward service/osp-gateway-service 8080:http


.PHONY: build
build:
	$(GRADLE) build

.PHONY: buildImage
buildImage:
	$(GRADLE) bootBuildImage

.PHONY: clean
clean:
	$(GRADLE) clean