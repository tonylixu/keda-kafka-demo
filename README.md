# Keda Introduction
This repository contains a basic example of deploying KEDA to a local kubernetes. Reference: https://github.com/IBM/keda-introduction

## Pre-requisite
* Docker and Kubernetes installed. I am using Docker Desktop (v2.3.0.2) with Kubernetes (v1.16.5) enabled.

## Run the example

1. Install the latest version of strimzi. There are other installation methods but the following will suffice for experimentation. At time of writing, the latest version is 0.18.0.
    ```sh
    kubectl create ns kafka
    kubectl apply -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
    ```
1. Deploy the Kafka cluster. The following is an example from strimzi for a kafka cluster with a persistent storage.
    ```sh
    kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka 
    ```
1. Deploy the Kafka topic.
    ```sh
    kubectl apply -f deploy/kafka-topic.yaml
    ```
1. Create the keda-sample namespace.
    ```sh
    kubectl create ns keda-sample
    ```
1. Build and deploy the application.
    ```sh
    docker build -f Dockerfile -t simple-consumer .

    kubectl apply -f deploy/consumer-deployment.yml -n keda-sample
    kubectl apply -f deploy/consumer-service.yml -n keda-sample
    ```
1. Install KEDA. We will be using the version that is currently the latest which is 1.4.1. The following will apply the KEDA resources straight from their GitHub so cloning or downloading won't be necessary. Other installation options can be found in their deploy documentation at https://keda.sh/docs/1.4/deploy/.
    ```sh
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/crds/keda.k8s.io_scaledobjects_crd.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/crds/keda.k8s.io_triggerauthentications_crd.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/00-namespace.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/01-service_account.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/10-cluster_role.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/11-role_binding.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/12-operator.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/20-metrics-cluster_role.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/21-metrics-role_binding.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/22-metrics-deployment.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/23-metrics-service.yaml
    kubectl apply -f https://raw.githubusercontent.com/kedacore/keda/v1.4.1/deploy/24-metrics-api_service.yaml
    ```
1. Deploy the Kafka scaler.
    ```sh
    kubectl apply -f deploy/consumer-scaler.yml
    ```
1. Send messages to the consumer. The following will create a pod for the kafka producer in the kafka namespace which will delete itself after you are done with it.
    ```sh
    kubectl -n kafka run kafka-producer -ti --image=strimzi/kafka:0.18.0-kafka-2.5.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic messages
    ```
1. Send multiple messages in quick succession.
    ```sh
    kubectl -n kafka run kafka-producer -ti --image=strimzi/kafka:0.18.0-kafka-2.5.0 --rm=true --restart=Never -- bin/kafka-producer-perf-test.sh --topic messages --throughput 3 --num-records 100 --record-size 4 --producer-props bootstrap.servers=my-cluster-kafka-bootstrap:9092
    ```

Related Links:
* <a href="https://strimzi.io/quickstarts/">Strimzi Quickstart Guide</a>
* <a href="https://github.com/kedacore/keda">KEDA GitHub</a>
* <a href="https://keda.sh/docs/1.4/">KEDA v1.4 documentation</a>
