package org.muyi.chapter04.dao;

import org.muyi.chapter04.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User getByName(String name);
}
