# Chọn base image JDK
FROM eclipse-temurin:21-jdk-jammy

# Tạo thư mục app
WORKDIR /app

# Copy file jar đã build
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Command để chạy jar
ENTRYPOINT ["java","-jar","app.jar"]