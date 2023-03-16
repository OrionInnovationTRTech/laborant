FROM adoptopenjdk/openjdk11:ubi as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
COPY .env .
RUN ./mvnw install -DskipTests -X
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM adoptopenjdk/openjdk11:ubi
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","tr.com.orioninc.laborant.LaborantApplication"]


