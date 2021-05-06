package com.ushkov.repository;

import com.ushkov.domain.User;

import java.util.List;

public interface UserRepository extends CrudOperations<Long, User> {
    List<User> findUsersByQuery(String query);

    Double getUserExpensiveCarPrice(Integer userId);
}
