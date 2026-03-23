package com.edu.university.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * TỪ ĐIỂN MÃ LỖI TOÀN HỆ THỐNG (ENTERPRISE STANDARD)
 * Quy tắc đặt tên Internal Code:
 * - SYS: Lỗi hệ thống chung (System)
 * - AUTH: Lỗi phân quyền, xác thực (Authentication/Authorization)
 * - STD: Lỗi hồ sơ Sinh viên, Khoa, Ngành (Student/Department)
 * - CRS: Lỗi dữ liệu Môn học, Lớp học phần (Course/Class Section)
 * - ENR: Lỗi nghiệp vụ Đăng ký tín chỉ (Enrollment)
 * - FIN: Lỗi nghiệp vụ Tài chính, Học phí (Finance)
 * - EXM: Lỗi nghiệp vụ Lịch thi (Examination)
 * - COM: Lỗi nghiệp vụ Cộng đồng (Community/Chat/Forum)
 */
@Getter
public enum ErrorCode {

    // === LỖI HỆ THỐNG CHUNG (SYS) ===
    INVALID_INPUT(400, "SYS_001", "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "SYS_002", "Chưa xác thực hoặc token hết hạn", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "SYS_003", "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "SYS_004", "Không tìm thấy tài nguyên", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(500, "SYS_500", "Lỗi hệ thống nội bộ! Vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),

    // Alias cho các lỗi hệ thống cũ để tránh lỗi biên dịch
    SYSTEM_ERROR(500, "SYS_500", "Lỗi hệ thống nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_NOT_FOUND(404, "SYS_004", "Không tìm thấy dữ liệu", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS(409, "SYS_409", "Dữ liệu đã tồn tại trong hệ thống", HttpStatus.CONFLICT),

    // === LỖI NGHIỆP VỤ XÁC THỰC (AUTH) ===
    USER_ALREADY_EXISTS(409, "AUTH_001", "Tên đăng nhập hoặc email đã tồn tại", HttpStatus.CONFLICT),
    USER_NOT_FOUND(404, "AUTH_002", "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(400, "AUTH_003", "Thông tin đăng nhập không chính xác", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(400, "AUTH_004", "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    OTP_INVALID(400, "AUTH_005", "Mã OTP không chính xác", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(400, "AUTH_006", "Giá trị role không hợp lệ", HttpStatus.BAD_REQUEST),

    // === LỖI NGHIỆP VỤ SINH VIÊN & HÀNH CHÍNH (STD) ===
    STUDENT_NOT_FOUND(404, "STD_001", "Không tìm thấy hồ sơ sinh viên", HttpStatus.NOT_FOUND),
    STUDENT_CODE_EXISTS(409, "STD_002", "Mã sinh viên đã tồn tại", HttpStatus.CONFLICT),
    FACULTY_NOT_FOUND(404, "STD_003", "Không tìm thấy Khoa", HttpStatus.NOT_FOUND),
    FACULTY_CODE_EXISTS(409, "STD_004", "Mã Khoa đã tồn tại", HttpStatus.CONFLICT),
    MAJOR_NOT_FOUND(404, "STD_005", "Không tìm thấy Ngành học", HttpStatus.NOT_FOUND),
    MAJOR_CODE_EXISTS(409, "STD_006", "Mã Ngành đã tồn tại", HttpStatus.CONFLICT),
    CLASS_NOT_FOUND(404, "STD_007", "Không tìm thấy Lớp hành chính", HttpStatus.NOT_FOUND),
    CLASS_CODE_EXISTS(409, "STD_008", "Mã Lớp hành chính đã tồn tại", HttpStatus.CONFLICT),

    // === LỖI NGHIỆP VỤ ĐÀO TẠO & MÔN HỌC (CRS) ===
    COURSE_NOT_FOUND(404, "CRS_001", "Không tìm thấy môn học", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS(409, "CRS_002", "Mã môn học đã tồn tại", HttpStatus.CONFLICT),
    INVALID_PREREQUISITE(400, "CRS_003", "Môn tiên quyết không hợp lệ", HttpStatus.BAD_REQUEST),
    CLASS_SECTION_NOT_FOUND(404, "CRS_004", "Không tìm thấy lớp học phần", HttpStatus.NOT_FOUND),

    // === LỖI NGHIỆP VỤ ĐĂNG KÝ TÍN CHỈ (ENR) ===
    ENROLLMENT_NOT_FOUND(404, "ENR_001", "Không tìm thấy thông tin đăng ký", HttpStatus.NOT_FOUND),
    ALREADY_ENROLLED(409, "ENR_002", "Sinh viên đã đăng ký lớp học phần này rồi", HttpStatus.CONFLICT),
    CLASS_SECTION_CLOSED(400, "ENR_003", "Lớp học phần này hiện không mở đăng ký", HttpStatus.BAD_REQUEST),
    REGISTRATION_NOT_OPEN(400, "ENR_004", "Chưa đến thời gian đăng ký tín chỉ", HttpStatus.BAD_REQUEST),
    REGISTRATION_CLOSED(400, "ENR_005", "Đã hết hạn đăng ký tín chỉ", HttpStatus.BAD_REQUEST),
    CLASS_SECTION_FULL(409, "ENR_006", "Lớp học phần đã đầy sinh viên", HttpStatus.CONFLICT),
    SCHEDULE_CONFLICT(409, "ENR_007", "Phát hiện trùng lịch học với môn khác", HttpStatus.CONFLICT),
    PREREQUISITE_NOT_MET(400, "ENR_008", "Chưa đạt điều kiện môn tiên quyết", HttpStatus.BAD_REQUEST),
    MAX_CREDITS_EXCEEDED(400, "ENR_009", "Vượt quá số tín chỉ tối đa cho phép", HttpStatus.BAD_REQUEST),
    ACADEMIC_SUSPENSION(403, "ENR_010", "Bạn đang bị đình chỉ hoặc cấm đăng ký", HttpStatus.FORBIDDEN),
    TUITION_DEBT_EXISTS(403, "ENR_011", "Bạn chưa hoàn thành học phí các kỳ trước", HttpStatus.FORBIDDEN),

    // Alias cho EnrollmentService cũ
    REGISTRATION_NOT_STARTED(400, "ENR_004", "Chưa đến thời gian đăng ký tín chỉ", HttpStatus.BAD_REQUEST),
    REGISTRATION_ENDED(400, "ENR_005", "Đã hết hạn đăng ký tín chỉ", HttpStatus.BAD_REQUEST),
    TUITION_DEBT(403, "ENR_011", "Bạn còn nợ học phí", HttpStatus.FORBIDDEN),
    CREDIT_LIMIT_EXCEEDED(400, "ENR_009", "Vượt quá giới hạn tín chỉ", HttpStatus.BAD_REQUEST),
    CLASS_FULL(409, "ENR_006", "Lớp đã đầy", HttpStatus.CONFLICT),

    // === LỖI NGHIỆP VỤ TÀI CHÍNH (FIN) ===
    TUITION_FEE_NOT_FOUND(404, "FIN_001", "Không tìm thấy hồ sơ học phí", HttpStatus.NOT_FOUND),
    TUITION_ALREADY_PAID(409, "FIN_002", "Học phí kỳ này đã được đóng đủ", HttpStatus.CONFLICT),

    // Alias cho TuitionService cũ
    TUITION_RECORD_NOT_FOUND(404, "FIN_001", "Không tìm thấy bản ghi học phí", HttpStatus.NOT_FOUND),
    INVALID_PAYMENT_AMOUNT(400, "FIN_003", "Số tiền thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),

    // === LỖI NGHIỆP VỤ LỊCH THI (EXM) ===
    INVALID_EXAM_TIME(400, "EXM_001", "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    EXAM_ROOM_CONFLICT(409, "EXM_002", "Phòng thi đã có lịch thi khác trong khung giờ này", HttpStatus.CONFLICT),
    EXAM_STUDENT_CONFLICT(409, "EXM_003", "Phát hiện trùng lịch thi của sinh viên", HttpStatus.CONFLICT),

    // === LỖI NGHIỆP VỤ CỘNG ĐỒNG (COM) ===
    TOPIC_NOT_FOUND(404, "COM_001", "Không tìm thấy Chủ đề thảo luận", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(404, "COM_002", "Không tìm thấy Bình luận", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND_CHAT(404, "COM_003", "Không tìm thấy người dùng để nhắn tin", HttpStatus.NOT_FOUND);

    private final int status; // HTTP Status Code nguyên bản (dành cho body JSON)
    private final String internalCode; // Mã lỗi định danh nội bộ
    private final String message; // Thông báo lỗi tiếng Việt chuẩn hóa
    private final HttpStatus httpStatus; // Đối tượng HttpStatus của Spring

    ErrorCode(int status, String internalCode, String message, HttpStatus httpStatus) {
        this.status = status;
        this.internalCode = internalCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}