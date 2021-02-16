#!/usr/bin/env bash

# Options
while getopts ':x:' opt; do
  case $opt in
  \?) echo "Invalid option: -$OPTARG" >&2; exit 1 ;;
  :)  echo "Option -$OPTARG requires an argument." >&2; exit 1 ;;
  x)
    case $OPTARG in
    efk)  declare NO_EFK && echo "EFK will not be deployed" ;;
    crdb) declare NO_COCKROACHDB && echo "CockroachDB will not be deployed" ;;
    osp)  declare NO_OSP && echo "OSP Services will not be deployed" ;;
    *) echo "Invalid argument for -$opt: $OPTARG" >&2; exit 1 ;;
    esac
    ;;
  esac
done

# EFK
if [ -z ${NO_EFK+x} ]; then
  pushd deployment/vendor/efk && skaffold run && popd || exit 1
fi

# CockroachDB
if [ -z ${NO_COCKROACHDB+x} ]; then
  skaffold run -f deployment/vendor/cockroachdb/skaffold.yaml
  echo 'Waiting for CockroachDB pods to be ready...'
  kubectl -n cockroachdb wait --for=condition=Ready pod -l app.kubernetes.io/component=cockroachdb --timeout=60s
  kubectl -n cockroachdb run -it --rm cockroach-client --image=cockroachdb/cockroach --restart=Never --command -- \
    ./cockroach sql --insecure --host=cockroachdb-public.cockroachdb \
    --execute="$(cat deployment/vendor/cockroachdb/init-db.sql)"
  kubectl -n cockroachdb port-forward service/cockroachdb-public 26257:grpc 8257:http > /dev/null &
fi

# OSP Services
if [ -z ${NO_OSP+x} ]; then
  skaffold run --port-forward
fi