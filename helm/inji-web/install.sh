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

function ensure_injiweb_host() {
  # Check if mosip-injiweb-host is present in global config map of config-server
  if ! kubectl get cm config-server --from configmap/global -o jsonpath='{.data.mosip-injiweb-host}' | grep -q "$MOSIP_INIEB_HOST"; then
    echo "Adding $MOSIP_INIEB_HOST to config-server global config map"
    kubectl patch configmap config-server -n $NS --type json -p "[{\"op\": \"add\", \"path\": \"/data/mosip-injiweb-host\", \"value\": \"$MOSIP_INIEB_HOST\"}]"
    kubectl -n config-server set env --keys=mosip-injiweb-host --from configmap/global deployment/config-server --prefix=SPRING_CLOUD_CONFIG_SERVER_OVERRIDES_SOFTHSM_MOCK_IDENTITY_SYSTEM_
    # Restart config-server
    kubectl -n config-server  get deploy -o name |  xargs -n1 -t  kubectl -n config-server rollout status
  else
    echo "$MOSIP_INIEB_HOST already present in config-server global config map"
  fi
}



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
