package ru.aston.repository.impl;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.entity.UserEntity;
import ru.aston.exception.UserNotFoundException;
import ru.aston.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserEntity> findAll() {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        String hql1 = "From UserEntity";
        List<UserEntity> list = session.createQuery(hql1).getResultList();
        tr.commit();
        return list;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        UserEntity user = session.find(UserEntity.class, id);
        tr.commit();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        String hql1 = "FROM UserEntity u WHERE u.email = :email";
        Optional<UserEntity> findUser = session.createQuery(hql1)
                .setParameter("email", email)
                .uniqueResultOptional();
        tr.commit();
        return findUser;

    }

    @Override
    public UserEntity save(UserEntity user) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        try {
            session.persist(user);
            tr.commit();
        } catch (ConstraintViolationException e) {
            tr.rollback();
            LOGGER.error(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public Optional<UserEntity> update(UserEntity user) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        UserEntity updatedUser = null;
        try {
            updatedUser = session.merge(user);
            tr.commit();
        } catch (ConstraintViolationException e) {
            tr.rollback();
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.ofNullable(updatedUser);
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tr = session.beginTransaction();
        try {
            UserEntity deleteUser = session.find(UserEntity.class, id);
            if (Objects.isNull(deleteUser)) {
                LOGGER.warn("User not found for delete with id = {}", id);
                throw new UserNotFoundException("User not found for delete with id = " + id);
            }
            session.remove(deleteUser);
            tr.commit();
        } catch (ConstraintViolationException e) {
            tr.rollback();
            LOGGER.error(e.getMessage(), e);
        }
    }
}