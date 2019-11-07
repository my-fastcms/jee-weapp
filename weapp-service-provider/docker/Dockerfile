FROM openjdk:8-jdk-alpine
LABEL maintainer="Wangjun<wjun_java@163.com>"

WORKDIR /opt/weapp-service

ADD . /opt/weapp-service

RUN chmod +x /opt/weapp-service/weapp.sh &&  \
    rm -rf /opt/weapp-service/Dockerfile

EXPOSE 8080

CMD ["/opt/weapp-service/weapp.sh", "start"]
