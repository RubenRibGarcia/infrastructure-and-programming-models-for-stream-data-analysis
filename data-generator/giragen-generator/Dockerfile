FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine

WORKDIR /
COPY target/giragen-generator-shaded.jar giragen-generator.jar
ENTRYPOINT ["java", "-Dconfig.file=/opt/isel/thesis/impads/spds-benchmark/giragen/conf/application.conf", "-jar", "giragen-generator.jar"]