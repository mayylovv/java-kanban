package tasks;

import status.Status;

import java.time.Instant;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Instant startTime;
    private long duration;

    public Task(String title, String description) {
        this.description = description;
        this.title = title;
        this.status = Status.NEW;
    }

    public Task(String title, String description, Instant startTime, long duration) {
        this.description = description;
        this.title = title;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getEndTime() {
        long SECONDS_IN_MINUTE = 60L;
        return startTime.plusSeconds(duration * SECONDS_IN_MINUTE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(title, task.title)
                && status.equals(task.status) && Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", title='" + title + "'" +
                ", description='" + description + "'" + ", status=" + status + ", startTime=" + getStartTime().toEpochMilli() +
                ", endTime=" + getEndTime().toEpochMilli() + ", duration=" + getDuration() + "}";
    }
}
