package com.example.aqaratgreen.Advice;

import com.example.aqaratgreen.Api.ApiException;
import com.example.aqaratgreen.Api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class ControllerAdvice {


    // اذا كان العميل ليس موجود في النظام
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity RunTimeException(RuntimeException e){
        String message = e.getMessage();
        return  ResponseEntity.status(400).body(message);
    }


    // استخدام الميثود الخاطئه مع (الباث الصحيح او الطلب الصحيح)
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String msg = e.getMessage();
        return ResponseEntity.status(400).body(new ApiResponse(msg));
    }


    // تهتم في validation مثلا (طول كلمة غير مناسب) او (ادخال ارقام لاتتطابق مع القيود) او (حقل مطلوب)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getFieldError().getDefaultMessage();
        return ResponseEntity.status(400).body(new ApiResponse(msg));
    }


    //  تتعامل مع اذا كان الباث غير صحيح NOT FOUND
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> NoResourceFoundException(NoResourceFoundException e) {
        String msg = e.getMessage();
        return ResponseEntity.status(404).body(new ApiResponse(msg));
    }

    //تتعامل مع body JSON اذا كان فيه اخطاء مثلا (') او ( , )
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String msg = e.getMessage();
        return ResponseEntity.status(400).body(new ApiResponse(msg));
    }


    //   يتعامل مع (تكرار القيم الفريدة) و (القيم غير صحيحة) أو (مفقودة) في قاعدة البيانات
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> DataIntegrityViolationException(DataIntegrityViolationException e){
        String msg=e.getMessage();
        return ResponseEntity.status(400).body(new ApiResponse(msg));
    }


      // ادخال بيانات لاتتوافق مع القيود مثلا ادخال (ارقام في حقل يجب ان يكون قيمة نصية)
      @ExceptionHandler(value = ValidationException.class)
      public ResponseEntity ValidationException(ValidationException e){
        String message = e.getMessage();
        return  ResponseEntity.status(400).body(message);
      }


}
