package com.edu.university.modules.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation tÃ¹y chá»‰nh dÃ¹ng Ä‘á»ƒ Ä‘Ã¡nh dáº¥u cÃ¡c hÃ m cáº§n lÆ°u lá»‹ch sá»­ thao tÃ¡c (Audit Log)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {

    // HÃ nh Ä‘á»™ng thá»±c hiá»‡n (VÃ­ dá»¥: "CREATE", "UPDATE", "DELETE", "IMPORT")
    String action();

    // Äá»‘i tÆ°á»£ng bá»‹ tÃ¡c Ä‘á»™ng (VÃ­ dá»¥: "Student", "Course", "TuitionFee")
    String entityName();
}
