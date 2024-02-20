# Production deployment using Helm charts
The below scripts will help the adopters to deploy INJI_WEB services in kubernetes environment.

## Prerequisites
- Kubernetes Cluster with minimum 3 nodes
- [Helm](https://helm.sh/docs/intro/install/)
- kubectl
- Ingress
- Domain URL (domain url mapped to kubernetes cluster)

## Deployment steps

### Clone the repo
```bash
git clone https://github.com/mosip/inji-web
cd deployment/helm
```

### Pre check
Make sure from the current directory you're able to run the below commands
```bash
kubectl cluster-info
kubectl get nodes
kubectl get ns
helm version
```

### Create namespace
```bash
kubectl create ns inji-web
```
`Feel free to use a different name for the namespace. Use the same name in the reset of the commands.`


### Create secrets (IF NEEDED)
Convert all the passwords/secrets into base64 format and update these values in `values.yaml` file
**Secrets**
- EXAMPLE_SECRET_1: can be a secret Token 
- Example_secret_2: Can be a database Connection URL with username and password (Example: postgres://{{databse_usename}}:{{database_password}}@localhost:5432/{{database_name:injiweb}})


### Modify configuration values
Configuration values related to esignet redirect URL, base URL and momito base URL etc should be modified in values.yaml file.


### Deploy helm charts
```bash
helm upgrade --install --namespace=inji-web inji-web helm_charts --create-namespace
```
**Output**
```
Release "inji-web" does not exist. Installing it now.
NAME: inji-web
LAST DEPLOYED: Thu May  4 17:02:08 2023
NAMESPACE: inji-web
STATUS: deployed
REVISION: 1
```

**Check if all the pods are running**
```bash
kubectl get pods -n inji-web
```


