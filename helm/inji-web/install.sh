#!/bin/bash
# Installs inji-web helm charts
## Usage: ./install.sh [kubeconfig]

if [ $# -ge 1 ] ; then
  export KUBECONFIG=$1
fi

NS=injiweb
CHART_VERSION=0.0.1-develop


read -p "Please provide injiwebhost (eg: injiweb.sandbox.xyz.net ) : " MOSIP_INIEB_HOST

if [ -z "MOSIP_INIEB_HOST" ]; then
  echo "INJIWEB Host not provided; EXITING;"
  exit 0;
fi

# Check if MOSIP_INJIWEB_HOST is present under configmap/global of configserver
if kubectl get cm global -n config-server -o jsonpath={.data.mosip-injiweb-host} | grep -q "MOSIP_INJIWEB_HOST"; then
    echo "MOSIP_INJIWEB_HOST is already present in configmap/global of configserver"
else
    echo "MOSIP_INJIWEB_HOST is not present in configmap/global of configserver"
    # Add injiweb host to global
    kubectl patch configmap global --type merge -p "{\"data\": {\"mosip-injiweb-host\": \"$MOSIP_INIEB_HOST\"}}"
    # Add the host
    kubectl set env deployment/config-server SPRING_CLOUD_CONFIG_SERVER_OVERRIDES_MOSIP_ESIGNET_INJIWEB_HOST=$MOSIP_INJIWEB_HOST -n config-server
    # Restart the configserver deployment
    kubectl -n config-server get deploy -o name | xargs -n1 -t kubectl -n config-server rollout restart
fi


echo Create $NS namespace
kubectl create ns $NS

function ensure_injiweb_host() {
  # Check if mosip-injiweb-host is present in global config map of config-server
  if ! kubectl get cm config-server -n $NS -o jsonpath='{.data.mosip-esignet-host}' | grep -q 'mosip-injiweb-host'; then
    echo "Adding mosip-injiweb-host to config-server global config map"
    kubectl patch configmap config-server -n $NS --type json -p '[{"op": "add", "path": "/data/mosip-injiweb-host", "value": "injiweb.sandbox.xyz.net"}]'
    # Restart config-server
    kubectl rollout restart deployment config-server -n $NS
  else
    echo "mosip-injiweb-host already present in config-server global config map"
  fi
}

function installing_inji-web() {
  echo Istio label
  kubectl label ns $NS istio-injection=enabled --overwrite

  helm repo add mosip https://mosip.github.io/mosip-helm
  helm repo update

  echo Copy configmaps
  ./copy_cm.sh

  ESIGNET_HOST=$(kubectl get cm global -o jsonpath={.data.mosip-esignet-host})
  INJI_HOST=$(kubectl get cm global -o jsonpath={.data.mosip-injiweb-host})
  echo Installing INJIWEB
  helm -n $NS install injiweb mosip/inji-web \
  -f values.yaml \
  --set esignet_redirect_url=$ESIGNET_HOST \
  --set istio.hosts\[0\]=$INJI_HOST \
  --version $CHART_VERSION

  kubectl -n $NS  get deploy -o name |  xargs -n1 -t  kubectl -n $NS rollout status

  echo Installed inji-web
  return 0
}

# set commands for error handling.
set -e
set -o errexit   ## set -e : exit the script if any statement returns a non-true return value
set -o nounset   ## set -u : exit the script if you try to use an uninitialised variable
set -o errtrace  # trace ERR through 'time command' and other functions
set -o pipefail  # trace ERR through pipes
installing_inji-web   # calling function
