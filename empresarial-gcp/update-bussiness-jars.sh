
#!/bin/bash

# =========================
# CONFIG
# =========================
PROJECT_ID="prj-hypertech-qa"
REGION="us-central1"
REPOSITORY="docker-repo"
NAMESPACE="apps"
IMAGE_TAG="prod"

# carpeta -> deployment
declare -A MICROSERVICES=(
  ["bussiness-banking"]="bussiness"
  ["domain-banking"]="domain-banking"
  ["domain-banking-core"]="domain-banking-core"
)

# =========================
# LOG
# =========================
log() {
  echo -e "\n $1\n"
}

error() {
  echo " ERROR: $1"
  exit 1
}

# =========================
# DEPLOY
# =========================
deploy_service() {
  SERVICE=$1
  DEPLOYMENT=$2

  APP_PATH="./$SERVICE"
  JAR_FILE=$(ls $APP_PATH/target/*.jar 2>/dev/null | head -n1)
  IMAGE_NAME="$REGION-docker.pkg.dev/$PROJECT_ID/$REPOSITORY/$SERVICE:$IMAGE_TAG"
  
  log "Actualizando: $SERVICE → $DEPLOYMENT"

  # Validar JAR
  if [ ! -f "$JAR_FILE" ]; then
    error "No existe $JAR_FILE"
  fi

  # Build imagen
  log "Docker build..."
  docker build -t $IMAGE_NAME $APP_PATH || error "Docker build falló"

  # Autenticarse en GCP para push
  log "Autenticando Docker con GCP..."
  gcloud auth configure-docker "$REGION-docker.pkg.dev" || error "Auth Docker falló"

  # Push
  log "Subiendo imagen a Artifact Registry..."
  docker push $IMAGE_NAME || error "docker push falló"

  # Actualizar deployment
  log "Aplicando nueva imagen al deployment..."
  kubectl set image deployment/$DEPLOYMENT \
    $DEPLOYMENT=$IMAGE_NAME -n $NAMESPACE || error "set image falló"

  # Reiniciar deployment
  log "Reiniciando deployment..."
  kubectl rollout restart deployment $DEPLOYMENT -n $NAMESPACE || error "Restart falló"

  # Esperar rollout
  kubectl rollout status deployment $DEPLOYMENT -n $NAMESPACE

  # Logs
  POD=$(kubectl get pods -n $NAMESPACE -l app=$DEPLOYMENT -o jsonpath='{.items[0].metadata.name}')
  log "Logs de $POD"
  timeout 60 kubectl logs -f $POD -n $NAMESPACE
}

# =========================
# MAIN
# =========================
for SERVICE in "${!MICROSERVICES[@]}"; do
  deploy_service "$SERVICE" "${MICROSERVICES[$SERVICE]}"
done

log "Deploy terminado"
