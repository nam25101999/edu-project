package com.edu.university.modules.report.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation tùy chỉnh dùng để đánh dấu các hàm cần lưu lịch sử thao tác (Audit Log)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {

    // Hành động thực hiện (Ví dụ: "CREATE", "UPDATE", "DELETE", "IMPORT")
    String action();

    // Đối tượng bị tác động (Ví dụ: "Student", "Course", "TuitionFee")
    String entityName();
}