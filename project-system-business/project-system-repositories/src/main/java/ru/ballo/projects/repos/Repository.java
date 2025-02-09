package ru.ballo.projects.repos;

import java.util.List;
import java.util.Optional;

public interface Repository <E> {

    E create(E e);

    E update(E e);

    E deleteById(Long id);

    Optional<E> getById(Long id);

    List<E> getAll();
}
