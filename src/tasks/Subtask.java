package tasks;

import java.time.Instant;
import java.util.Objects;
import status.Status;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int epicId, String title, String description) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, String description, Instant startTime, long duration) {
        super(title, description, startTime, duration);
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
                "', description='" + getDescription() + "', status=" + getStatus() + ", startTime=" +
                getStartTime().toEpochMilli() + ", endTime=" + getEndTime().toEpochMilli() +
                ", duration=" + getDuration() + "}";
    }
}

