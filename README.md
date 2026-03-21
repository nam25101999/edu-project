🎓 Hệ Thống Quản Lý Đào Tạo Tín Chỉ Đại Học (University Management System)

<div align="center">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Java-17-ED8B00%3Fstyle%3Dfor-the-badge%26logo%3Djava%26logoColor%3Dwhite" alt="Java" />
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F%3Fstyle%3Dfor-the-badge%26logo%3Dspring-boot%26logoColor%3Dwhite" alt="Spring Boot" />
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Spring_Security-JWT-6DB33F%3Fstyle%3Dfor-the-badge%26logo%3Dspring-security%26logoColor%3Dwhite" alt="Spring Security" />
<img src="https://www.google.com/search?q=https://img.shields.io/badge/SQL_Server-Cloud-CC2927%3Fstyle%3Dfor-the-badge%26logo%3Dmicrosoft-sql-server%26logoColor%3Dwhite" alt="SQL Server" />
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Docker-Enabled-2496ED%3Fstyle%3Dfor-the-badge%26logo%3Ddocker%26logoColor%3Dwhite" alt="Docker" />
</div>

Dự án là một giải pháp phần mềm chuyển đổi số toàn diện cho môi trường Đại học. Hệ thống quản lý toàn bộ vòng đời của sinh viên từ lúc nhập học, đăng ký tín chỉ, đóng học phí, tham gia cộng đồng diễn đàn, cho đến khi xét duyệt học vụ và tốt nghiệp.

Đặc biệt, hệ thống được thiết kế theo kiến trúc Modular Monolith chuẩn Enterprise, phân chia rõ ràng theo từng Domain Driven (Miền nghiệp vụ), sẵn sàng để scale (mở rộng) lên Microservices trong tương lai.

📑 Mục lục

✨ Tính năng nổi bật

💻 Công nghệ sử dụng

🏗 Kiến trúc hệ thống

🚀 Hướng dẫn cài đặt (Docker)

📖 Tài liệu API (Swagger)

📸 Hình ảnh minh họa

✨ Tính năng nổi bật

Hệ thống được chia thành 6 Module nghiệp vụ chính, hoạt động độc lập nhưng liên kết chặt chẽ với nhau:

1. 🔐 Module Xác Thực & Bảo Mật (Auth)

Đăng nhập, Đăng ký phân quyền Role-based (ADMIN, LECTURER, STUDENT).

Xác thực bảo mật bằng JWT (JSON Web Token).

Quên mật khẩu & Đặt lại mật khẩu an toàn thông qua Mã OTP gửi qua Email.

2. 📚 Module Quản Lý Đào Tạo & Tín Chỉ (Course & Enrollment)

Quản lý Khoa, Ngành, Lớp học hành chính.

Mở lớp học phần (Class Section), giới hạn sĩ số sinh viên tối đa.

Thuật toán xếp lịch thông minh: Tự động phát hiện và chặn nếu sinh viên đăng ký trùng lịch học, hoặc giảng viên xếp trùng phòng thi, trùng giờ thi.

Nhập điểm (Chuyên cần, Giữa kỳ, Cuối kỳ), tự động quy đổi điểm chữ (A, B, C, D, F) và tính GPA hệ 4.0.

Đánh giá học vụ tự động (Bình thường / Cảnh báo / Đình chỉ).

3. 💰 Module Tài Chính (Finance)

Tự động tính toán học phí theo tổng số tín chỉ đăng ký thực tế.

Hỗ trợ thanh toán linh hoạt (Đóng toàn bộ / Đóng một phần).

Lưu vết lịch sử giao dịch và xuất hóa đơn điện tử (text/csv).

4. 💬 Module Cộng Đồng (Community)

Diễn đàn (Forum): Thảo luận chung toàn trường hoặc hỏi đáp trực tiếp trong từng môn học.

Chat trực tiếp (Direct Message): Nhắn tin 1-1 giữa sinh viên và giảng viên/admin.

Hệ thống Thông báo (Notification): Gửi thông báo toàn trường, thông báo riêng cho 1 lớp học phần, hoặc thông báo cá nhân.

5. 📊 Module Báo Cáo & Dữ Liệu (Report)

Import/Export Excel (Apache POI): Nhập/Xuất hàng nghìn dữ liệu sinh viên, bảng điểm, thời khóa biểu chỉ với 1 click.

Dashboard Thống kê: Biểu đồ tỷ lệ sinh viên theo khoa, tỷ lệ đậu/rớt, Top sinh viên xuất sắc.

Enterprise Audit Log (AOP): Hệ thống theo dõi thao tác người dùng ngầm. Ghi lại mọi địa chỉ IP, Thiết bị (User-Agent), Payload dữ liệu gửi lên và thời gian xử lý API (Execution Time) để phục vụ Debug và truy vết bảo mật.

💻 Công nghệ sử dụng

Backend Core: Java 21, Spring Boot 3.2.5, Spring Data JPA, Hibernate.

Security: Spring Security 6, JWT, BCrypt.

Database: Microsoft SQL Server (Lưu trữ an toàn trên Cloud).

Libraries/Tools: Lombok, Apache POI (Xử lý file Excel), JavaMailSender (Gửi Email SMTP), Springdoc OpenAPI (Swagger).

DevOps & Deploy: Docker, Docker Compose, Multi-stage builds.

🏗 Kiến trúc hệ thống (Modular Monolith)

Dự án áp dụng triệt để nguyên tắc Package by Feature, loại bỏ hoàn toàn sự kết dính (Tight Coupling) cấp độ cơ sở dữ liệu giữa các module:

src/main/java/com/university/
├── common/         # Chứa Cấu hình, Exception Handler, Security Filters
├── modules/        # Nơi chứa các miền nghiệp vụ độc lập
│   ├── auth/       # -> Service xác thực, cấp Token, OTP Email
│   ├── student/    # -> Quản lý thông tin Sinh viên, Khoa, Ngành
│   ├── course/     # -> Quản lý Môn học, Lớp học phần, Xếp lịch thi
│   ├── enrollment/ # -> Xử lý Đăng ký tín chỉ, Điểm số, Tính GPA
│   ├── finance/    # -> Quản lý Công nợ, Học phí, Thanh toán
│   ├── community/  # -> Quản lý Diễn đàn, Chat 1-1, Thông báo đẩy
│   ├── report/     # -> Thống kê Dashboard, Import/Export Excel, Audit Log
│   └── ui/         # -> Giao diện Admin Dashboard (Thymeleaf/HTML)
└── BackendApplication.java


🚀 Hướng dẫn cài đặt bằng Docker

Với Docker, bạn không cần phải cài đặt môi trường Java rườm rà hay config Database phức tạp. Mọi thứ đã được bọc sẵn!

1. Yêu cầu trước khi cài đặt:

Máy tính đã cài đặt và đang chạy Docker Desktop.

Git.

2. Các bước khởi chạy:

Mở Terminal (Command Prompt) và chạy lần lượt các lệnh sau:

# Clone source code về máy
git clone [https://github.com/nam25101999/du-an-giao-duc.git](https://github.com/nam25101999/du-an-giao-duc.git)

# Di chuyển vào thư mục dự án
cd du-an-giao-duc/backend

# Khởi chạy toàn bộ hệ thống bằng Docker Compose
docker-compose up -d --build


3. Trải nghiệm hệ thống:

Sau khi Terminal báo Started BackendApplication, bạn có thể truy cập:

🌐 Giao diện Admin (Web UI): http://localhost:8080/admin-ui/login

📚 Tài liệu API (Swagger): http://localhost:8080/swagger-ui/index.html

(Tài khoản Admin mặc định để test: admin / password123)

📖 Tài liệu API (Swagger)

Tất cả các RESTful API của dự án đều được sinh tài liệu tự động qua Swagger. Giao diện trực quan cho phép bạn test API trực tiếp trên trình duyệt.

Cách test API cần bảo mật:

Gọi API POST /api/auth/login với tài khoản hợp lệ.

Copy chuỗi token trả về.

Kéo lên đầu trang Swagger, bấm vào nút Authorize (màu xanh lá) và dán token vào.

📸 Hình ảnh minh họa

Dashboard Thống kê

Quản lý Sinh viên

<img src="https://www.google.com/search?q=https://via.placeholder.com/600x350.png%3Ftext%3DAdmin%2BDashboard%2BScreenshot" alt="Dashboard">

<img src="https://www.google.com/search?q=https://via.placeholder.com/600x350.png%3Ftext%3DStudent%2BManagement%2BScreenshot" alt="Students">

Swagger OpenAPI

Excel Import/Export

<img src="https://www.google.com/search?q=https://via.placeholder.com/600x350.png%3Ftext%3DSwagger%2BUI%2BScreenshot" alt="Swagger">

<img src="https://www.google.com/search?q=https://via.placeholder.com/600x350.png%3Ftext%3DExcel%2BReport%2BScreenshot" alt="Excel">

(Lưu ý: Bạn hãy thay thế link ảnh placeholder ở trên bằng ảnh chụp màn hình thực tế dự án của bạn để README đẹp nhất nhé!)

Phát triển bởi nam25101999 ☕ Nếu bạn thấy dự án này hữu ích, đừng quên cho mình 1 ⭐ nhé!
