# ==========================================
# STAGE 1: BUILD
# Dùng image Maven + JDK 17 để build source code
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file pom.xml vào trước để tải dependencies
# (Mẹo tối ưu: Giúp cache lại các thư viện, lần sau build sẽ rất nhanh)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code vào và tiến hành build ra file .jar
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: RUN

FROM eclipse-temurin:21-jdk

# Cài đặt timezone (Tùy chọn nhưng cần thiết cho ứng dụng ở VN)
RUN apt-get update && apt-get install -y tzdata
ENV TZ=Asia/Ho_Chi_Minh

WORKDIR /app

# Copy file .jar đã build từ STAGE 1 sang STAGE 2
COPY --from=builder /app/target/*.jar app.jar

# Tạo thư mục uploads để lưu avatar
RUN mkdir -p uploads/avatars

# Mở cổng 8080 để bên ngoài gọi vào
EXPOSE 8080

# Lệnh chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]