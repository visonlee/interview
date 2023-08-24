
# Demo App for DevOps

#build package
mvn clean package

# docker build
docker build --build-arg FILENAME=target/*.jar -t lws/demo-springboot:v1 .

# query the param for k8s
kubectl explain deploy.spec.strategy.rollingUpdate

# kubectl
cd to k8s folder
kubectl get all
kubectl -apply -f .

# call api call every second
while true; do curl 'http://127.0.0.1:31754/hello'; sleep 1; done # call every second

kubectl get svc

# query rollout status
kubectl rollout status deployment/demo-springboot-app-deployment

# query rollout history
kubectl rollout history deployment/demo-springboot-app-deployment

# clean up
kubectl delete svc demo-springboot-app-service
kubectl delete deployment demo-springboot-app-deployment
docker rmi -f lws/demo-springboot:v1(v2)

or use kubectl delete -f .


