package ds_fites1_2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Client {

    private final List<Project> cProjects;
    private Clock clock;
    private int read;
    static final int SEG = 1000;


    public Client() throws InterruptedException, IOException,
            ClassNotFoundException {
        this.cProjects = new ArrayList<Project>();

        System.out.println("Enter '1', '2', '3' o '4' "
                + "to run the application. 1(A.1) - "
                + "2(A.2) - 3(A.1-Serializable) - "
                + "4(Fita2 with report generation): ");
        Scanner pt = new Scanner(System.in);
        final int option = pt.nextInt();

        switch (option) {
            case 1:
                this.main1();
                break;
            case 2:
                this.main2();
                break;
            case 3:
                System.out.println("Enter '1 (read)' "
                        + "o '0 (start)' to read from a file");
                pt = new Scanner(System.in);
                read = pt.nextInt();
                this.main3();
                break;
            case 4:
                this.main4();
                break;
            default:
                System.out.println("Invalid option");
                break;

        }
    }

    public final void print() {
        final Visitor imp = new Print();
        System.out.printf("%-33s", " ");
        System.out.printf("%-26s", "Start date");
        System.out.printf("%-26s", "End date");
        System.out.printf("%-26s", "Total time");
        System.out.println();
        System.out.println("-----------------"
                + "------------------------------------------------");

        for (final Project p : cProjects) {
            p.accept(imp);
        }
    }


    public final void saveData() throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("datos.dat"));

        for (final Project p : cProjects) {
            oos.writeObject(p);
        }
        oos.close();
    }

    public final void readData() throws IOException, ClassNotFoundException {

        final FileInputStream fitxer = new FileInputStream("datos.dat");

        final ObjectInputStream ois = new ObjectInputStream(fitxer);

        while (fitxer.available() > 0) {

            this.cProjects.add((Project) ois.readObject());
        }

        ois.close();
    }

    // Test A.1
    public final void main1() throws InterruptedException,
            IOException, ClassNotFoundException {

        final Project p1 = new Project("P1", "Project1");
        this.cProjects.add(p1);
        final Project p2 = new Project("P2", "Project2");
        final Task t1 = new Task("T1", "Task1");
        final Task t2 = new Task("T2", "Task2");
        final Task t3 = new Task("T3", "Task3");

        p1.addGroup(t3);
        p1.addGroup(p2);
        p2.addGroup(t1);
        p2.addGroup(t2);

        t3.start();
        Thread.sleep(3 * Client.SEG);
        t3.stop();

        System.out.println("We should see table 1.");

        Thread.sleep(7 * Client.SEG);
        t2.start();
        Thread.sleep(10 * Client.SEG);
        t2.stop();
        t3.start();
        Thread.sleep(2 * Client.SEG);
        t3.stop();

        System.out.println("We should see table 2.");

    }

    // Test A.2
    public final void main2() throws InterruptedException,
            IOException, ClassNotFoundException {

        final Project p1 = new Project("P1", "Project1");
        this.cProjects.add(p1);
        final Project p2 = new Project("P2", "Project2");
        final Task t1 = new Task("T1", "Task1");
        final Task t2 = new Task("T2", "Task2");
        final Task t3 = new Task("T3", "Task3");

        p1.addGroup(t3);
        p1.addGroup(p2);
        p2.addGroup(t1);
        p2.addGroup(t2);

        t3.start();
        Thread.sleep(4 * Client.SEG);
        t2.start();
        Thread.sleep(2 * Client.SEG);
        t3.stop();
        Thread.sleep(2 * Client.SEG);
        t1.start();
        Thread.sleep(4 * Client.SEG);
        t1.stop();
        Thread.sleep(2 * Client.SEG);
        t2.stop();
        Thread.sleep(4 * Client.SEG);
        t3.start();
        Thread.sleep(2 * Client.SEG);
        t3.stop();

        System.out.println("We should see table 3.");

    }

    // Test A.1 with Serializable
    public final void main3() throws InterruptedException,
            IOException, ClassNotFoundException {

        if (read == 1) {
            this.readData();
            this.print();
        } else {
            this.main1();
            this.saveData();
        }

        System.out.println("The content should be saved.");


    }

    public final void main4() throws InterruptedException,
            IOException, ClassNotFoundException {

        final Project pRoot = new Project("Root", "Root");

        final Project p1 = new Project("P1", "P1");
        final Project p2 = new Project("P2", "P2");

        pRoot.addGroup(p1);
        pRoot.addGroup(p2);

        final Project p12 = new Project("P1_2", "P1_2");
        p1.addGroup(p12);

        final Task t1 = new Task("T1", "T1");
        final Task t2 = new Task("T2", "T2");
        final Task t3 = new Task("T3", "T3");
        final Task t4 = new Task("T4", "T4");

        p1.addGroup(t1);
        p1.addGroup(t2);
        p12.addGroup(t4);
        p2.addGroup(t3);

        t1.start();
        t4.start();

        Thread.sleep(4 * Client.SEG);
        final Date d1 = new Date();

        t1.stop();
        t2.start();

        Thread.sleep(6 * Client.SEG);
        t2.stop();
        t4.stop();
        t3.start();

        Thread.sleep(4 * Client.SEG);
        final Date d2 = new Date();
        t3.stop();
        t2.start();


        Thread.sleep(2 * Client.SEG);
        t3.start();

        Thread.sleep(4 * Client.SEG);
        t2.stop();
        t3.stop();


/*
        RDetailed report1 = new RDetailed(pRoot, new Period(d1, d2));
        report1.writeReport();
        report1.buildReport(new FText("DetailedReport.txt"));
        report1.buildReport(new FHTML("DetailedReport.html"));

        RBasic report2 = new RBasic(pRoot, new Period(d1, d2));
        report2.writeReport();
        report2.buildReport(new FText("BasicReport.txt"));
        report2.buildReport(new FHTML("BasicReport.html"));
        <
 */

        System.out.println("Basic and detailed report created correctly");
    }

}
