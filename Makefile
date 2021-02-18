#!/usr/bin/env make -f

GRADLE = ./gradlew

NS_EFK = efk
NS_COCKROACHDB = cockroachdb
NS_OSP = default

.PHONY: deploy
deploy: deploy/efk deploy/cockroachdb deploy/osp

.PHONY: deploy/efk
deploy/efk:
	pushd deployment/vendor/efk && skaffold run && popd

.PHONY: deploy/cockroachdb
deploy/cockroachdb:
	skaffold run -f deployment/vendor/cockroachdb/skaffold.yaml
	kubectl -n $(NS_COCKROACHDB) wait --for=condition=Ready pod -l app.kubernetes.io/component=cockroachdb \
		--timeout=60s
	kubectl -n $(NS_COCKROACHDB) run -i --rm cockroach-client --image=cockroachdb/cockroach --restart=Never \
		--command -- ./cockroach sql --insecure --host=cockroachdb-public.cockroachdb \
		< deployment/vendor/cockroachdb/init-db.sql

.PHONY: deploy/osp
deploy/osp:
	skaffold run

.PHONY: pf
pf: pf/efk pf/cockroachdb pf/osp

.PHONY: pf/efk
pf/efk:
	kubectl -n $(NS_EFK) port-forward service/kibana-kibana 5601:http

.PHONY: pf/cockroachdb
pf/cockroachdb:
	kubectl -n $(NS_COCKROACHDB) port-forward service/cockroachdb-public 26257:grpc 8257:http

.PHONY: pf/osp
pf/osp:
	kubectl -n $(NS_OSP) port-forward service/osp-eureka-service 8761:http
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