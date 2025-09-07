package ru.aston.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.aston.exception.DataBaseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class SqlScriptRunner {

    public static void executeSql(SessionFactory sessionFactory, String sqlFilePath) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            String sql = Files.lines(Path.of(sqlFilePath)).collect(Collectors.joining("\n"));
            String[] statements = sql.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    session.createNativeMutationQuery(statement).executeUpdate();
                }
            }

            transaction.commit();
        } catch (IOException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataBaseException("Failed to execute SQL script");
        }
    }
}
