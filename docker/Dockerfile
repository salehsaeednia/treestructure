FROM openjdk:8-jdk-alpine
RUN addgroup -S nodeGroup && adduser -S nodeUser -G nodeGroup
RUN mkdir -p /logs \
 && chown -R nodeUser:root /logs \
 && chmod -R 0775 /logs
USER nodeUser:nodeGroup
ARG JAR_FILE=treestructure-1.0.jar
EXPOSE 8585
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]