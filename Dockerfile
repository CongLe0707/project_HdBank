# Dùng image chính thức của OpenJDK 24
FROM openjdk:24-jdk-slim

# Thư mục làm việc trong container
WORKDIR /app

# Copy file jar vào container, đổi tên thành app.jar
COPY target/*.jar app.jar

# Mở port 8085 để truy cập từ máy ngoài
EXPOSE 8085

# Lệnh chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
