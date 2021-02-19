#!/usr/bin/env make -f

GRADLE = ./gradlew

NS_EFK = efk
NS_POSTGRESQL = default
NS_OSP = default

.PHONY: deploy
deploy: deploy/efk deploy/postgresql deploy/osp

.PHONY: deploy/efk
deploy/efk:
	skaffold run -f deployment/vendor/efk/skaffold.yaml
	kubectl apply -f deployment/vendor/efk/fluent-bit.yaml

.PHONY: deploy/postgresql
deploy/postgresql:
	kubectl apply -f deployment/vendor/postgresql/postgresql-init.yaml
	skaffold run -f deployment/vendor/postgresql/skaffold.yaml

.PHONY: deploy/osp
deploy/osp:
	skaffold run

.PHONY: pf
pf: pf/kibana pf/postgresql pf/eureka pf/gateway

.PHONY: pf/kibana
pf/kibana:
	kubectl -n $(NS_EFK) port-forward service/kibana-kibana 5601:http

.PHONY: pf/postgresql
pf/postgresql:
	kubectl -n $(NS_POSTGRESQL) port-forward service/postgresql 5432:tcp-postgresql

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