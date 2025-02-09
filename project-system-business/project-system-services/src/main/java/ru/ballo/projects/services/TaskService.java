package ru.ballo.projects.services;

import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.dto.task.TaskFilter;
import ru.ballo.projects.dto.task.UpdateTaskDTO;

import java.util.List;

public interface TaskService extends Service<TaskDTO, CreateTaskDTO> {
    List<TaskDTO> search(TaskFilter filter);
    TaskDTO updateStatus(Long id);
    TaskDTO update(UpdateTaskDTO dto);
}
