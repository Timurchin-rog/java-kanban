package service;

import model.Status;
import model.Subtask;
import model.Task;
import model.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(manager.getAllTasks(), "Хеш-таблица с задачами пуста");
        manager.removeAllTasks();
        final HashMap<Integer, Task> tasks = new HashMap<>();
        assertEquals(manager.getAllTasks(), tasks, "Хеш-таблица с задачами не пуста");
    }

    @Test
    @DisplayName("Должен удалять все эпики")
    void shouldRemoveAllEpics() {
        assertNotNull(manager.getAllEpics(), "Хеш-таблица с эпиками пуста");
        manager.removeAllEpics();
        final HashMap<Integer, Task> epics = new HashMap<>();
        assertEquals(manager.getAllEpics(), epics, "Хеш-таблица с эпиками не пуста");
    }

    @Test
    @DisplayName("Должен находить подзадачи определённого эпика")
    void shouldFindSubtaskOfEpic() {
        final ArrayList<Subtask> subTasksOfEpic = manager.getSubtasksOfEpic(epic.getId());
        assertEquals(subTasksOfEpic.size(), epic.subtasks.size(), "Разный размер списков");
        assertEquals(subTasksOfEpic.getFirst(), epic.subtasks.getFirst(), "Подзадачи не совпадают");

    }

    @Test
    @DisplayName("Должен находить задачи по ID")
    void shouldFindTasksById() {
        final int idTask = task.getId();
        final Task foundTask = manager.getTask(idTask);
        assertEquals(foundTask, manager.getAllTasks().get(idTask), "Задачи не совпадают");
    }

    @Test
    @DisplayName("Должен находить подзадачи по ID")
    void shouldFindSubtasksById() {
        final int idSubtask = subtask.getId();
        final Task foundSubtask = manager.getSubtask(idSubtask);
        assertEquals(foundSubtask, manager.getAllSubtasks().get(idSubtask), "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("Должен находить эпики по ID")
    void shouldFindEpicsById() {
        final int idEpic = epic.getId();
        final Task foundEpic = manager.getEpic(idEpic);
        assertEquals(foundEpic, manager.getAllEpics().get(idEpic), "Эпики не совпадают");
    }

    @Test
    @DisplayName("Должен обновлять подзадачи и вычислять статус эпика")
    void shouldUpdateSubtaskAndCalculateStatusOfEpic() {
        Subtask updatedSubtaskDone = new Subtask(Type.SUBTASK, "Test updatedSubtask", "DONE",
                "Test updatedSubtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0), epic.getId());
        manager.updateSubtask(subtask.getId(), updatedSubtaskDone);
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика не изменился");
        Subtask updatedSubtask = new Subtask(Type.SUBTASK, "Test updatedSubtask", "IN_PROGRESS",
                "Test updatedSubtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0), epic.getId());
        manager.updateSubtask(subtask.getId(), updatedSubtask);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");
    }

    @Test
    @DisplayName("Должен удалять подзадачу и из хеш-таблицы и из списочного массива эпика")
    void shouldRemoveSubtask() {
        final int idSubtask = subtask.getId();
        manager.removeSubtask(idSubtask);
        assertNull(manager.getAllSubtasks().get(idSubtask), "Подзадача не удалена из хеш-таблицы");
        assertFalse(epic.subtasks.contains(subtask), "Подзадача не удалена из списка подзадач эпика");
    }

    @Test
    @DisplayName("Должен получать историю просмотров")
    void shouldGetBrowsingHistory() {
        final int idTask = task.getId();
        manager.getTask(idTask);
        final int idSubtask = subtask.getId();
        manager.getSubtask(idSubtask);
        final int idEpic = epic.getId();
        manager.getEpic(idEpic);
        assertNotNull(manager.getHistory());
        assertEquals(manager.getHistory(), historyManager.getHistory(), "Истории просмотров не совпадают");
    }

    @Test
    @DisplayName("Должен вычислять время начала и окончания эпика")
    void shouldCalculateStatusAndTimeOfEpic() {
        Subtask subtask1 = manager.createSubtask(new Subtask(Type.SUBTASK, "Subtask1",
                "NEW", "Subtask1", Duration.ofMinutes(45),
                LocalDateTime.of(2984, 1, 1, 8, 0, 0), epic.getId()));
        assertEquals(epic.getStartTime(), subtask.getStartTime(),
                "Время начала эпика не соответствует времени начала саммой ранней подзадачи");
        assertEquals(epic.getEndTime(), subtask1.getEndTime(),
                "Время окончания эпика не соответствует времени окончания саммой поздней подзадачи");
    }

    @Test
    @DisplayName("Должен получать список задач по приоритету")
    void shouldGetPrioritizedListOfTask() {
        Subtask updatedSubtask = new Subtask(Type.SUBTASK, "Test updatedSubtask", "IN_PROGRESS",
                "Test updatedSubtask description", Duration.ofMinutes(10),
                LocalDateTime.of(1, 1, 1, 12, 0, 0), epic.getId());
        manager.updateSubtask(subtask.getId(), updatedSubtask);
        manager.removeTask(task.getId());
        System.out.println(manager.getPrioritizedTasks());
        assertEquals(manager.getPrioritizedTasks().size(), 2,
                "Размер списка по приоритету не соответствует");
        assertEquals(manager.getPrioritizedTasks().getFirst(), updatedSubtask,
                "Приоритет не работает");
    }

}
