package tasks;

import java.util.Objects;
import status.Status;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int epicId, String title, String description, Status status) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{epicId=" + getEpicId() + ", id=" + getId() + ", title='" + getTitle() +
                "', description='" + getDescription() + "', status=" + getStatus() + "}";
    }
}
