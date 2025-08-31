package ru.aston.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    @Email(message = "Проверьте email на корректность")
    private String email;

    @Min(value = 16, message = "Пользователь должен быть старше 16")
    @Max(value = 100, message = "Пользователь должен быть младше 100")
    private int age;

    public void print() {
        System.out.printf("\t%s %d %s\n", name, age, email);
    }
}
