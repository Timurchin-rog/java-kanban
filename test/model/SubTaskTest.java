package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Подзадача")
class SubTaskTest {
    @Test
    @DisplayName("должна совпадать со своей копией")
    void shouldSubTaskEqualsWithCopy() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        final int subTaskId = subTask.getId();
        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask.getId(), savedSubTask.getId(), "Подзадачи не совпадают - разные ID");
        assertEquals(subTask.getName(), savedSubTask.getName(), "Подзадачи не совпадают - разные названия");
        assertEquals(subTask.getStatus(), savedSubTask.getStatus(), "Подзадачи не совпадают - разные статусы");
        assertEquals(subTask.getDescription(), savedSubTask.getDescription(), "Подзадачи не совпадают - разные описания");

        final HashMap<Integer, SubTask> subTasks = taskManager.printAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask.getId(), subTasks.get(subTaskId).getId(), "Подзадачи не совпадают - разные ID");
        assertEquals(subTask.getName(), subTasks.get(subTaskId).getName(), "Подзадачи не совпадают - разные названия");
        assertEquals(subTask.getStatus(), subTasks.get(subTaskId).getStatus(), "Подзадачи не совпадают - разные статусы");
        assertEquals(subTask.getDescription(), subTasks.get(subTaskId).getDescription(), "Подзадачи не совпадают - разные описания");
    }
}