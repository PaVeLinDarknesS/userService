package ru.aston;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.enumeration.ActionEnum;
import ru.aston.handler.ActionResolver;

import java.util.Scanner;

public class Console {

    private final static Logger logger = LoggerFactory.getLogger(Console.class);

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать User Service:");

        ActionResolver resolver = new ActionResolver();

        while (true) {
            try {
                System.out.println("""
                        ________________________________________________
                        Введите 'LIST' для вывода всех User
                        Введите 'FIND' для поиска User по ID
                        Введите 'INSERT' для добавления нового User
                        Введите 'UPDATE' для изменения User
                        Введите 'DELETE' для удаления User по ID
                        Введите 'EXIT' для выхода из программы
                        """);

                resolver.resolve(ActionEnum.valueOf(scanner.nextLine().trim().toUpperCase()))
                        .doAction();
            } catch (Exception e) {
                logger.error("{} - {}", e.getClass(), e.getMessage());
                System.err.println(e.getMessage());
            }
        }
    }
}
