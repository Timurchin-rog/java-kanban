package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskManager")
class InMemoryTaskManagerTest {
    InMemoryHistoryManager historyManager;
    InMemoryTaskManager taskManager;
    Task task;
    SubTask subTask;
    Epic epic;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
    }

    @Test
    @DisplayName("должен удалять все задачи")
    void shouldRemoveAllTasks() {
        assertNotNull(taskManager.tasks, "Хеш-таблица с задачами пуста");
        taskManager.removeAllTasks();
        final HashMap<Integer, Task> tasks = new HashMap<>();
        assertEquals(taskManager.tasks, tasks, "Хеш-таблица с задачами не пуста");
    }

    @Test
    @DisplayName("должен удалять все подзадачи")
    void shouldRemoveAllSubTasks() {
        assertNotNull(taskManager.allSubTasks, "Хеш-таблица с подзадачами пуста");
        taskManager.removeAllSubTasks(epic);
        final HashMap<Integer, Task> subTasks = new HashMap<>();
        assertEquals(taskManager.allSubTasks, subTasks, "Хеш-таблица с подзадачами не пуста");
    }

    @Test
    @DisplayName("должен удалять все эпики")
    void shouldRemoveAllEpics() {
        assertNotNull(taskManager.epics, "Хеш-таблица с эпиками пуста");
        taskManager.removeAllEpics();
        final HashMap<Integer, Task> epics = new HashMap<>();
        assertEquals(taskManager.epics, epics, "Хеш-таблица с эпиками не пуста");
    }

    @Test
    @DisplayName("должен находить подзадачи определённого эпика")
    void shouldFindSubTaskOfEpic() {
        final ArrayList<SubTask> subTasksOfEpic = taskManager.getSubTasksOfEpic(epic);
        assertEquals(subTasksOfEpic.size(), epic.subTasks.size(), "Разный размер списков");
        assertEquals(subTasksOfEpic.getFirst(), epic.subTasks.getFirst(), "Подзадачи не совпадают");

    }

    @Test
    @DisplayName("должен находить задачи по ID")
    void shouldFindTasksById() {
        final int idTask = task.getId();
        final Task foundTask = taskManager.getTask(idTask);
        assertEquals(foundTask, taskManager.tasks.get(idTask), "Задачи не совпадают");
    }

    @Test
    @DisplayName("должен находить подзадачи по ID")
    void shouldFindSubTasksById() {
        final int idSubTask = subTask.getId();
        final Task foundSubTask = taskManager.getSubTask(idSubTask);
        assertEquals(foundSubTask, taskManager.allSubTasks.get(idSubTask), "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("должен находить эпики по ID")
    void shouldFindEpicsById() {
        final int idEpic = epic.getId();
        final Task foundEpic = taskManager.getEpic(idEpic);
        assertEquals(foundEpic, taskManager.epics.get(idEpic), "Эпики не совпадают");
    }

    @Test
    @DisplayName("должен обновлять подзадачи и вычислять состояние эпика")
    void shouldUpdateSubTaskAndCalculateStatusOfEpic() {
        taskManager.updateSubTask(epic, subTask, "Test SubTask", Status.DONE, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика не изменился");
        taskManager.updateSubTask(epic, subTask, "Test SubTask", Status.IN_PROGRESS, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");
    }

    @Test
    @DisplayName("должен удалять подзадачи и из хеш-таблицы и из списочного массива эпика")
    void shouldRemoveSubTask() {
        final int idSubTask = subTask.getId();
        taskManager.removeSubTask(idSubTask);
        assertNull(taskManager.allSubTasks.get(idSubTask), "Подзадача не удалена из хеш-таблицы");
        assertFalse(epic.subTasks.contains(subTask), "Подзадача не удалена из списка подзадач эпика");
    }

    @Test
    @DisplayName("должен получать историю просмотров")
    void shouldGetBrowsingHistory() {
        final int idTask = task.getId();
        taskManager.getTask(idTask);
        final int idSubTask = subTask.getId();
        taskManager.getSubTask(idSubTask);
        final int idEpic = epic.getId();
        taskManager.getEpic(idEpic);
        assertNotNull(taskManager.getHistory());
        System.out.println(taskManager.getHistory());
        System.out.println(historyManager.getHistory());
        assertEquals(taskManager.getHistory(), historyManager.getHistory(), "Истории просмотров не совпадают");
    }
}