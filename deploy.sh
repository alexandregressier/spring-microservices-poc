#!/usr/bin/env bash

# Options
while getopts ':x:' opt; do
  case $opt in
  \?) echo "Invalid option: -$OPTARG" >&2; exit 1 ;;
  :)  echo "Option -$OPTARG requires an argument." >&2; exit 1 ;;
  x)
    case $OPTARG in
    efk) declare NO_EFK && echo "EFK will not be deployed" ;;
    osp) declare NO_OSP && echo "OSP Services will not be deployed" ;;
    *) echo "Invalid argument for -$opt: $OPTARG" >&2; exit 1 ;;
    esac
    ;;
  esac
done

# EFK
if [ -z ${NO_EFK+x} ]; then
  skaffold run -f deployment/vendor/efk/skaffold.yaml
fi

# OSP Services
if [ -z ${NO_OSP+x} ]; then
  skaffold run --port-forward
fi