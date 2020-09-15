package ds_fites1_2;

// Class that implements the Visitor pattern
public interface Visitor {

    // This method prints the information
    // related to a Project (name, life period and time spent)
    void visitProject(Project p);

    // This method prints the information
    // related to a Task (name, life period and time spent)
    void visitTask(Task t);
}
