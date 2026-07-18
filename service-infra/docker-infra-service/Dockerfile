FROM bellsoft/liberica-runtime-container:jre-26-musl

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","app.jar"]