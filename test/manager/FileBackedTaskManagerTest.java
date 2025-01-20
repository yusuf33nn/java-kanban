package manager;

import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    public FileBackedTaskManagerTest() {
        super(new FileBackedTaskManager(Path.of("src/main/resources/tasks.csv")));
    }
}
