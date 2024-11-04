package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Предок различных реализаций менеджеров")
public class TaskManagerTest {
    InMemoryTaskManager manager;
    InMemoryHistoryManager historyManager;
    Task task;
    SubTask subTask;
    Epic epic;

    protected InMemoryTaskManager createManager() {
        historyManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

    @BeforeEach
    void init() {
        manager = createManager();
        task = manager.createTask(new Task(Type.TASK, "Test Task", Status.NEW, "Test Task description"));
        epic = manager.createEpic(new Epic(Type.EPIC, "Test Epic", Status.NEW, "Test Epic description"));
        subTask = manager.createSubTask(new SubTask(Type.SUBTASK, "Test SubTask",
                Status.NEW, "Test SubTask description", epic));
    }

    @Test
    @DisplayName("должен удалять все задачи")
    void shouldRemoveAllTasks() {
        assertNotNull(manager.tasks, "Хеш-таблица с задачами пуста");
        manager.removeAllTasks();
        final HashMap<Integer, Task> tasks = new HashMap<>();
        assertEquals(manager.tasks, tasks, "Хеш-таблица с задачами не пуста");
    }

    @Test
    @DisplayName("должен удалять все подзадачи")
    void shouldRemoveAllSubTasks() {
        assertNotNull(manager.allSubTasks, "Хеш-таблица с подзадачами пуста");
        manager.removeAllSubTasks(epic);
        final HashMap<Integer, Task> subTasks = new HashMap<>();
        assertEquals(manager.allSubTasks, subTasks, "Хеш-таблица с подзадачами не пуста");
    }

    @Test
    @DisplayName("должен удалять все эпики")
    void shouldRemoveAllEpics() {
        assertNotNull(manager.epics, "Хеш-таблица с эпиками пуста");
        manager.removeAllEpics();
        final HashMap<Integer, Task> epics = new HashMap<>();
        assertEquals(manager.epics, epics, "Хеш-таблица с эпиками не пуста");
    }

    @Test
    @DisplayName("должен находить подзадачи определённого эпика")
    void shouldFindSubTaskOfEpic() {
        final ArrayList<SubTask> subTasksOfEpic = manager.getSubTasksOfEpic(epic);
        assertEquals(subTasksOfEpic.size(), epic.subTasks.size(), "Разный размер списков");
        assertEquals(subTasksOfEpic.getFirst(), epic.subTasks.getFirst(), "Подзадачи не совпадают");

    }

    @Test
    @DisplayName("должен находить задачи по ID")
    void shouldFindTasksById() {
        final int idTask = task.getId();
        final Task foundTask = manager.getTask(idTask);
        assertEquals(foundTask, manager.tasks.get(idTask), "Задачи не совпадают");
    }

    @Test
    @DisplayName("должен находить подзадачи по ID")
    void shouldFindSubTasksById() {
        final int idSubTask = subTask.getId();
        final Task foundSubTask = manager.getSubTask(idSubTask);
        assertEquals(foundSubTask, manager.allSubTasks.get(idSubTask), "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("должен находить эпики по ID")
    void shouldFindEpicsById() {
        final int idEpic = epic.getId();
        final Task foundEpic = manager.getEpic(idEpic);
        assertEquals(foundEpic, manager.epics.get(idEpic), "Эпики не совпадают");
    }

    @Test
    @DisplayName("должен обновлять подзадачи и вычислять состояние эпика")
    void shouldUpdateSubTaskAndCalculateStatusOfEpic() {
        manager.updateSubTask(subTask, "Test SubTask", Status.DONE, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика не изменился");
        manager.updateSubTask(subTask, "Test SubTask", Status.IN_PROGRESS, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");
    }

    @Test
    @DisplayName("должен удалять подзадачи и из хеш-таблицы и из списочного массива эпика")
    void shouldRemoveSubTask() {
        final int idSubTask = subTask.getId();
        manager.removeSubTask(idSubTask);
        assertNull(manager.allSubTasks.get(idSubTask), "Подзадача не удалена из хеш-таблицы");
        assertFalse(epic.subTasks.contains(subTask), "Подзадача не удалена из списка подзадач эпика");
    }

    @Test
    @DisplayName("должен получать историю просмотров")
    void shouldGetBrowsingHistory() {
        final int idTask = task.getId();
        manager.getTask(idTask);
        final int idSubTask = subTask.getId();
        manager.getSubTask(idSubTask);
        final int idEpic = epic.getId();
        manager.getEpic(idEpic);
        assertNotNull(manager.getHistory());
        assertEquals(manager.getHistory(), historyManager.getHistory(), "Истории просмотров не совпадают");
    }
}

