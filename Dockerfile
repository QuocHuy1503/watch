# Stage 1: Build - sử dụng image có Maven 3.9.9 và JDK 21 sẵn
FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

# Copy file cấu hình trước
COPY pom.xml .

# Tải dependencies để cache
RUN mvn dependency:go-offline

# Copy mã nguồn và build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run - dùng JDK nhẹ để chạy JAR
FROM amazoncorretto:21-alpine

WORKDIR /app

# Copy file JAR từ build stage
COPY --from=build /app/target/watch-0.0.1-SNAPSHOT.jar .

# Mở port ứng dụngdocker build -t watch-app .
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/watch-0.0.1-SNAPSHOT.jar"]
