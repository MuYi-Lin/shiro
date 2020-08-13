package org.muyi.chapter04.dao;

import org.muyi.chapter04.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
