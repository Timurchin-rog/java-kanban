package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Подзадача")
class SubTaskTest {
    TaskManager taskManager;
    Epic epic;
    SubTask subTask;
    int subTaskId;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        subTaskId = subTask.getId();
    }

    @Test
    @DisplayName("должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("должна совпадать со своей копией из хеш-таблицы")
    void shouldEqualsWithCopyFromHashMap() {
        final HashMap<Integer, SubTask> subTasks = taskManager.printAllSubTasks();
        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(subTaskId), "Подзадачи не совпадают");
    }
}