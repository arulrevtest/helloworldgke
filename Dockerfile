FROM openjdk:latest
EXPOSE 8090
EXPOSE 3306
RUN mkdir /opt/application
COPY target/helloworld-1.0.0.jar /opt/application
CMD ["java","-jar","/opt/application/helloworld-1.0.0.jar"]