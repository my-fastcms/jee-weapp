FROM maven:3.6-jdk-8-alpine
LABEL maintainer="Wangjun<wjun_java@163.com>"

WORKDIR /opt/weapp-mobile

ADD . /opt/weapp-mobile

RUN chmod +x /opt/weapp-service/weapp.sh &&  \
    rm -rf /opt/weapp-service/Dockerfile

EXPOSE 8080

CMD ["/opt/weapp-mobile/weapp.sh", "start"]