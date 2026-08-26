#!/bin/bash

# =========================
# CONFIGURACIÓN
# =========================
PROJECT_ID="prj-hypertech-prod"
REGION="northamerica-south1"
REPO_NAME="banking-cluster-asp-prod"

NAMESPACE="apps"
DEPLOYMENT_NAME="angular-frontend"
CONTAINER_NAME="angular"

IMAGE_NAME="html-empresarial"
IMAGE_TAG="prod"

APP_PATH="./html-empresarial"

# =========================
# VARIABLES DERIVADAS
# =========================
LOCAL_IMAGE="$IMAGE_NAME:$IMAGE_TAG"
REMOTE_IMAGE="$REGION-docker.pkg.dev/$PROJECT_ID/$REPO_NAME/$IMAGE_NAME:$IMAGE_TAG"

# =========================
# FUNCIONES
# =========================
log() { echo -e "\n $1\n"; }
error() { echo -e "\n ERROR: $1\n"; exit 1; }

# =========================
# VALIDACIÓN DIST
# =========================
if [ ! -d "$APP_PATH/dist" ]; then
  error "No existe la carpeta dist (build Angular manual pendiente)"
fi

# =========================
# BUILD DOCKER
# =========================
log "Building Docker image local: $LOCAL_IMAGE"

docker build --no-cache -t $LOCAL_IMAGE $APP_PATH || error "Docker build falló"

# =========================
# TAG PARA GCP
# =========================
log "Tagging image para Artifact Registry"
docker tag $LOCAL_IMAGE $REMOTE_IMAGE || error "Docker tag falló"

# =========================
# AUTH DOCKER GCP
# =========================
log "Autenticando Docker con GCP"
gcloud auth configure-docker $REGION-docker.pkg.dev -q || error "Auth Docker GCP falló"

# =========================
# PUSH A GCP
# =========================
log "Pushing image a Artifact Registry"
docker push $REMOTE_IMAGE || error "Docker push falló"

# =========================
# FORZAR RECREACIÓN REAL DEL POD
# =========================
log "Escalando deployment Angular a 0 (NO afecta otros pods)"
kubectl scale deployment $DEPLOYMENT_NAME --replicas=0 -n $NAMESPACE || error "Scale 0 falló"

sleep 5

log "Escalando deployment Angular a 1"
kubectl scale deployment $DEPLOYMENT_NAME --replicas=1 -n $NAMESPACE || error "Scale 1 falló"

# =========================
# ESPERAR ROLLOUT
# =========================
log "Esperando rollout del deployment Angular"
kubectl rollout status deployment/$DEPLOYMENT_NAME -n $NAMESPACE || error "Rollout falló"

# =========================
# VALIDACIÓN IMAGEN ACTIVA
# =========================
POD=$(kubectl get pods -n $NAMESPACE -l app=$DEPLOYMENT_NAME -o jsonpath='{.items[0].metadata.name}')

log "Pod activo: $POD"
kubectl describe pod $POD -n $NAMESPACE | grep -E "Image:|Image ID:"

log "Deploy Angular FINALIZADO correctamente "


