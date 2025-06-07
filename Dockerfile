# Sử dụng Temurin JDK 21 Alpine (khớp với java.version của bạn)
FROM eclipse-temurin:21-jdk-alpine

# Thiết lập thư mục làm việc
WORKDIR /app

# Nếu cần giữ file tạm của Spring Boot
VOLUME /tmp

# Copy JAR đã build vào /app
COPY target/watch-0.0.1-SNAPSHOT.jar .

# (Tùy chọn) mở port nếu app của bạn lắng nghe 8080
EXPOSE 8080

# Chạy JAR
ENTRYPOINT ["java","-jar","watch-0.0.1-SNAPSHOT.jar"]
