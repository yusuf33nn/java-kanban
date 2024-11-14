package utils;

import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void check_getDefault() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertNotNull(taskManager.getHistoryManager());
    }
}