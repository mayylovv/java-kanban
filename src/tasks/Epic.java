package tasks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private Instant endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, Instant startTime, long duration) {
        super(title, description, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() + ", title='" + getTitle() + "', description='" + getDescription() +
                "', status=" + getStatus() + "', subtaskIds=" + subtaskIds + ", startTime=" + getStartTime().toEpochMilli() +
                ", endTime=" + getEndTime().toEpochMilli() + ", duration=" + getDuration() + "};";
    }
}