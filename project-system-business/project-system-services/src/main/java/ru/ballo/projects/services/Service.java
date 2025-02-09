package ru.ballo.projects.services;

import java.util.List;

public interface Service <E, D> {
    E create(D d);
    E update(D d);
    E deleteFromStorage(Long id);
    E get(Long id);
    List<E> getAll();
}
