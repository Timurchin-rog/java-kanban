package service;

import model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;
import service.memory.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TaskManager")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        historyManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

    @Test
    @DisplayName("Должен удалять все задачи")
    void shouldRemoveAllTasks() {
        assertNotNull(manager.tasks, "Хеш-таблица с задачами пуста");
        manager.removeAllTasks();
        final HashMap<Integer, Task> tasks = new HashMap<>();
        assertEquals(manager.tasks, tasks, "Хеш-таблица с задачами не пуста");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи")
    void shouldRemoveAllSubTasks() {
        assertNotNull(manager.allSubTasks, "Хеш-таблица с подзадачами пуста");
        manager.removeAllSubTasks(epic);
        final HashMap<Integer, Task> subTasks = new HashMap<>();
        assertEquals(manager.allSubTasks, subTasks, "Хеш-таблица с подзадачами не пуста");
    }

    @Test
    @DisplayName("Должен удалять все эпики")
    void shouldRemoveAllEpics() {
        assertNotNull(manager.epics, "Хеш-таблица с эпиками пуста");
        manager.removeAllEpics();
        final HashMap<Integer, Task> epics = new HashMap<>();
        assertEquals(manager.epics, epics, "Хеш-таблица с эпиками не пуста");
    }

    @Test
    @DisplayName("Должен находить подзадачи определённого эпика")
    void shouldFindSubTaskOfEpic() {
        final ArrayList<SubTask> subTasksOfEpic = manager.getSubTasksOfEpic(epic);
        assertEquals(subTasksOfEpic.size(), epic.subTasks.size(), "Разный размер списков");
        assertEquals(subTasksOfEpic.getFirst(), epic.subTasks.getFirst(), "Подзадачи не совпадают");

    }

    @Test
    @DisplayName("Должен находить задачи по ID")
    void shouldFindTasksById() {
        final int idTask = task.getId();
        final Task foundTask = manager.getTask(idTask);
        assertEquals(foundTask, manager.tasks.get(idTask), "Задачи не совпадают");
    }

    @Test
    @DisplayName("Должен находить подзадачи по ID")
    void shouldFindSubTasksById() {
        final int idSubTask = subTask.getId();
        final Task foundSubTask = manager.getSubTask(idSubTask);
        assertEquals(foundSubTask, manager.allSubTasks.get(idSubTask), "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("Должен находить эпики по ID")
    void shouldFindEpicsById() {
        final int idEpic = epic.getId();
        final Task foundEpic = manager.getEpic(idEpic);
        assertEquals(foundEpic, manager.epics.get(idEpic), "Эпики не совпадают");
    }

    @Test
    @DisplayName("Должен обновлять подзадачи и вычислять статус эпика")
    void shouldUpdateSubTaskAndCalculateStatusOfEpic() {
        SubTask updatedSubTaskDone = new SubTask(Type.SUBTASK, "Test updatedSubTask", Status.DONE,
                "Test updatedSubTask description", 10, "01.01.2000, 12:00", epic);
        manager.updateSubTask(subTask, updatedSubTaskDone);
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика не изменился");
        SubTask updatedSubTask = new SubTask(Type.SUBTASK, "Test updatedSubTask", Status.IN_PROGRESS,
                "Test updatedSubTask description", 10, "01.01.2000, 12:00", epic);
        manager.updateSubTask(subTask, updatedSubTask);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");
    }

    @Test
    @DisplayName("Должен удалять подзадачи и из хеш-таблицы и из списочного массива эпика")
    void shouldRemoveSubTask() {
        final int idSubTask = subTask.getId();
        manager.removeSubTask(idSubTask);
        assertNull(manager.allSubTasks.get(idSubTask), "Подзадача не удалена из хеш-таблицы");
        assertFalse(epic.subTasks.contains(subTask), "Подзадача не удалена из списка подзадач эпика");
    }

    @Test
    @DisplayName("Должен получать историю просмотров")
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

    @Test
    @DisplayName("Должен вычислять время начала и окончания эпика")
    void shouldCalculateStatusAndTimeOfEpic() {
        SubTask subTask1 = manager.createSubTask(new SubTask(Type.SUBTASK, "SubTask1",
                Status.NEW, "SubTask1", 45, "01.01.2984, 08:00", epic));
        assertEquals(epic.getStartTime(), subTask.getStartTime(),
                "Время начала эпика не соответствует времени начала саммой ранней подзадачи");
        assertEquals(epic.getEndTime(), subTask1.getEndTime(),
                "Время окончания эпика не соответствует времени окончания саммой поздней подзадачи");
    }

    @Test
    @DisplayName("Должен получать список задач по приоритету")
    void shouldGetPrioritizedListOfTask() {
        SubTask updatedSubTask = new SubTask(Type.SUBTASK, "Test updatedSubTask", Status.IN_PROGRESS,
                "Test updatedSubTask description", 10, "01.01.0001, 12:00", epic);
        manager.updateSubTask(subTask, updatedSubTask);
        manager.removeTask(task.getId());
        assertEquals(manager.getPrioritizedTasks().size(), 2,
                "Размер списка по приоритету не соответствует");
        assertEquals(manager.getPrioritizedTasks().getFirst(), updatedSubTask,
                "Приоритет не работает");
    }

}
