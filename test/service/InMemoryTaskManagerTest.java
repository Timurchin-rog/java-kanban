package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskManager")
class InMemoryTaskManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

    @Test
    @DisplayName("должен удалять все элементы")
    void shouldRemoveAllElements() {
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        assertNotNull(taskManager.tasks, "Хеш-таблица с задачами пуста");
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        assertNotNull(taskManager.epics, "Хеш-таблица с эпиками пуста");
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        assertNotNull(taskManager.allSubTasks, "Хеш-таблица с подзадачами пуста");
        taskManager.removeAllTasks();
        final HashMap<Integer, Task> tasks1 = new HashMap<>();
        assertEquals(taskManager.tasks, tasks1, "Хеш-таблица с задачами не пуста");
        taskManager.removeAllSubTasks(epic);
        final HashMap<Integer, Task> subTasks1 = new HashMap<>();
        assertEquals(taskManager.allSubTasks, subTasks1, "Хеш-таблица с эпиками не пуста");
        taskManager.removeAllEpics();
        final HashMap<Integer, Task> epics1 = new HashMap<>();
        assertEquals(taskManager.epics, epics1, "Хеш-таблица с подзадачами не пуста");
    }

    @Test
    @DisplayName("должен находить подзадачи определённого эпика")
    void shouldFindSubTaskOfEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("Test Epic 1", Status.NEW, "Test Epic 1 description"));
        Epic epic2 = taskManager.createEpic(new Epic("Test Epic 2", Status.NEW, "Test Epic 2 description"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Test SubTask 1", Status.NEW, "Test SubTask 1 description", epic1));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Test SubTask 2", Status.NEW, "Test SubTask 2 description", epic2));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Test SubTask 3", Status.NEW, "Test SubTask 3 description", epic2));
        final ArrayList<SubTask> subTasksOfEpic = taskManager.getSubTasksOfEpic(epic1);
        assertEquals(subTasksOfEpic.size(), epic1.subTasks.size(), "Разный размер списков");
        assertEquals(subTasksOfEpic.getFirst().getName(), epic1.subTasks.getFirst().getName(), "Названия подзадач не совпадают");
        assertEquals(subTasksOfEpic.getFirst().getStatus(), epic1.subTasks.getFirst().getStatus(), "Статусы подзадач не совпадают");
        assertEquals(subTasksOfEpic.getFirst().getDescription(), epic1.subTasks.getFirst().getDescription(), "Описания подзадач не совпадают");
        assertEquals(subTasksOfEpic.getFirst().getEpic(), epic1.subTasks.getFirst().getEpic(), "Эпики подзадач не совпадают");
    }

    @Test
    @DisplayName("должен находить элемены по ID")
    void shouldFindElementsById() {
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        final int idTask = task.getId();
        final Task foundTask = taskManager.getTask(idTask);
        assertEquals(foundTask.getName(), taskManager.tasks.get(idTask).getName(), "Названия задач не совпадают");
        assertEquals(foundTask.getStatus(), taskManager.tasks.get(idTask).getStatus(), "Статусы задач не совпадают");
        assertEquals(foundTask.getDescription(), taskManager.tasks.get(idTask).getDescription(), "Описания задач не совпадают");
        final int idSubTask = subTask.getId();
        final Task foundSubTask = taskManager.getSubTask(idSubTask);
        assertEquals(foundSubTask.getName(), taskManager.allSubTasks.get(idSubTask).getName(), "Названия подзадач не совпадают");
        assertEquals(foundSubTask.getStatus(), taskManager.allSubTasks.get(idSubTask).getStatus(), "Статусы подзадач не совпадают");
        assertEquals(foundSubTask.getDescription(), taskManager.allSubTasks.get(idSubTask).getDescription(), "Описания подзадач не совпадают");
        final int idEpic = epic.getId();
        final Task foundEpic = taskManager.getEpic(idEpic);
        assertEquals(foundEpic.getName(), taskManager.epics.get(idEpic).getName(), "Названия эпиков не совпадают");
        assertEquals(foundEpic.getStatus(), taskManager.epics.get(idEpic).getStatus(), "Статусы эпиков не совпадают");
        assertEquals(foundEpic.getDescription(), taskManager.epics.get(idEpic).getDescription(), "Описания эпиков не совпадают");
    }

    @Test
    @DisplayName("должен обновлять подзадачи и вычислять состояние эпика")
    void shouldUpdateSubTaskAndCalculateStatusOfEpic() {
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        taskManager.updateSubTask(epic, subTask, "Test SubTask", Status.DONE, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика не изменился");
        taskManager.updateSubTask(epic, subTask, "Test SubTask", Status.IN_PROGRESS, "Test SubTask description");
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");
    }

    @Test
    @DisplayName("должен удалять подзадачи и из хеш-таблицы и из списочного массива эпика")
    void shouldCorrectlyRemoveSubTask() {
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
        final int idSubTask = subTask.getId();
        taskManager.removeSubTask(idSubTask);
        assertNull(taskManager.allSubTasks.get(idSubTask), "Подзадача не удалена из хеш-таблицы");
        assertFalse(epic.subTasks.contains(subTask), "Подзадача не удалена из списка подзадач эпика");
    }

    @Test
    @DisplayName("должен получать историю просмотров")
    void shouldGetBrowsingHistory() {
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
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