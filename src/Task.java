import java.util.Objects;

public class Task {
    private int id;
    private final String title;
    private final String description;
    private String status;

    public Task(String title, String description, String status) {
        this.description = description;
        this.title = title;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(title, task.title)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", title='" + title + "'" +
                ", description='" + description + "'" + ", status=" + status + "}";
    }

}