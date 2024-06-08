import java.util.ArrayList;

public class Epic extends Task{

    ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }
}
