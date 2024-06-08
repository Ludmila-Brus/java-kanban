public class Task {

    int id;
    String title;
    String description;
    Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }
}
