package com.ds.timetracker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ds_fites1_2.FHTML;
import ds_fites1_2.FText;
import ds_fites1_2.Group;
import ds_fites1_2.IDsGenerator;
import ds_fites1_2.Interval;
import ds_fites1_2.Period;
import ds_fites1_2.Project;
import ds_fites1_2.Clock;
import ds_fites1_2.RBasic;
import ds_fites1_2.RDetailed;
import ds_fites1_2.Task;
import es.dmoral.toasty.Toasty;


/**
 * Llegeix, manté en memòria i desa l'arbre de projectes, tasques i intervals, i
 * en gestiona tant la navegació per ell com el cronometrat de tasques, quan
 * així es sol·licita des de les classes que capturen els events d'interacció
 * amb l'usuari. A més, envia a les Activity que ho demanen la llista de les
 * dades d'activitat o d'interval que s'han de mostrar.
 *
 * @author joans
 * @version 26 gener 2012
 */
public class TreeManagerService extends Service implements Updateable {

    /**
     * Nom de la classe per fer aparèixer als missatges de logging del LogCat.
     *
     * @see Log
     */
    private final String tag = getClass().getSimpleName();

    /**
     * El node arrel de l'arbre de projectes, tasques i intervals. Aquest node
     * és doncs especial perquè no existeix per a l'usuari, només serveix per
     * contenir la llista d'activitats del primer nivell que si veu l'usuari.
     */
    private Project root;

    /**
     * Observable que actualitza les tasques que estan sent cronometrades en
     * invocar el seu mètode update().
     */
    private Clock clock;

    /**
     * Arxiu on es desa tot l'arbre de projectes, tasques i intervals.
     */

    private final String fileName = "timetracker.dat";
    private final String fileNameConfig = "config.dat";

    /**
     * En navegar per l'arbre de projectes i tasques, és el projecte pare de la
     * llista d'activitats mostrada per últim cop gràcies a la classe
     * <code>LlistaActivitatsActivity</code>, o bé la tasca pare dels intervals
     * mostrats a la activitat <code>LlistaIntervalsActivity</code>. El seu
     * valor inicial és el projecte arrel de tot l'abre, {@link #root}
     */

    private Group actualGroup;

    /**
     * El servei consisteix en processar, com en el cas d'engegar i parar
     * cronometre de tasca, o be processar i retornar unes dades, com en la
     * resta, ja que cal actualitzar el la activitat actual (tasca o projecte) i
     * enviar la llista d'activitats filles o intervals, segons el cas. Per tal
     * de retornar dades, dissenyem aquest intent.
     */

    public static final String SEND_ITEMS = "send_items";
    public static final String SEND_CONFIG = "send_config";
    public static final String SEND_TASKS_RUNNING = "send_tasks_running";
    public static final String SEND_ARE_TASKS_RUNNING = "send_are_tasks_running";

    /**
     * Rep els "intents" que envien <code>LlistaActivitatsActivity</code> i
     * <code>LlistaIntervalsActivity</code>. El receptor els rep tots (no hi ha
     * cap filtre) i els diferència per la seva "action". Veure {@link TreeManagerService.Receptor}
     * per saber quins són.
     */
    private TreeManagerService.Receptor receptor;

    /**
     * Handler que ens permet actualitzar la interfase d'usuari quan hi ha
     * alguna tasca que s'està cronometrant. Concretament, actualitza les
     * Activity que mostren activitats i intervals. S'engega i es para a
     * {@link TreeManagerService.Receptor#onReceive}.
     */
    private Updater updateIU;

    /**
     * Llista de tasques que estan sent cronometrades en cada moment, que
     * mantenim per tal que en parar el servei les puguem deixar de cronometrar
     * i fer que no es desin com si ho fossin i llavors tenir dades
     * inconsistents en tornar a carregar l'arbre.
     */
    private final ArrayList<Task> tasksRunning = new ArrayList<Task>();

    private ConfigApp configApp;

    public final void readTree() {
        // DONE : Això fora millor fer-ho en un altre thread per evitar que la
        // lectura d'un arxiu molt gran pugui trigui massa i provoqui que la
        // aplicació perdi responsiveness o pitjor, que aparegui el diàleg
        // ANR = application is not responding, demanant si volem forçar el
        // tancament de la aplicació o esperar.
        // La solució deu ser fer servir una AsyncTask, tal com s'explica a
        // l'article "Painless Threading" de la documentació del Android SDK.
        // Veure'l a la versió local o a
        // developer.android.com/resources/articles/painless-threading.html

        Log.d("TAG", "carrega arbre d'activitats");
        try {
            final FileInputStream fips = openFileInput(fileName);
            final ObjectInputStream in = new ObjectInputStream(fips);
            root = (Project) in.readObject();
            in.close();

            Log.d(tag, "Arbre llegit d'arxiu");
        } catch (final FileNotFoundException e) {
            Log.d(tag, "L'arxiu no es troba, fem un arbre buit");
            root = new Project("TimeTracker", "TimeTracker");
        } catch (final ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        try {
            final FileInputStream fips2 = openFileInput(fileNameConfig);
            final ObjectInputStream in2 = new ObjectInputStream(fips2);
            configApp = (ConfigApp) in2.readObject();
            in2.close();
        } catch (final FileNotFoundException e) {
            Log.d(tag, "L'arxiu no es troba, fem un arbre buit");
            configApp = new ConfigApp();
        } catch (final ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Desa l'arbre de projectes, tasques i intervals en un arxiu propi de la
     * aplicació. El mecanisme emprat per desar és serialitzar l'arrel.
     */
    // DONE : Això fora millor fer-ho en un altre thread per evitar que
    // l'escriptura d'un arxiu molt gran pugui trigui massa i provoqui que la
    // aplicació perdi "responsiveness".
    // DONE : si es demanés de desar l'arbre mentre alguna tasca s'estés
    // cronometrant, quedaria registrat així ? Seria un problema després,
    // quan es llegís l'arbre, per que aquest tasca no es podria cronometrar
    // altre cop. Comprovar-ho.
    public final void writeTree() {
        {
            Log.d("TAG", "desa arbre activitats");
            try {
                final FileOutputStream fops = this.openFileOutput(this.fileName,
                        Context.MODE_PRIVATE);
                final ObjectOutputStream out = new ObjectOutputStream(fops);
                out.writeObject(this.root);
                out.close();
            } catch (final FileNotFoundException e) {
                Log.d(this.tag, "L'arxiu no es troba");
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            Toasty.info(this, R.string.TreeSavedMSG, Toasty.LENGTH_LONG, true).show();
        }

    }

    public final void writeConfig() {
        try {
            this.configApp.setID_status(IDsGenerator.Instance(0).getIDNotModified());
            final FileOutputStream fops2 = this.openFileOutput(this.fileNameConfig,
                    Context.MODE_PRIVATE);
            final ObjectOutputStream out2 = new ObjectOutputStream(fops2);
            out2.writeObject(this.configApp);
            out2.close();
        } catch (final FileNotFoundException e) {
            Log.d(this.tag, "L'arxiu no es troba");
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * El nostre servei no està lligat a cap activitat per tal de compartir
     * dades. No és una bona opció per que el servei és destruït quan deixa
     * d'estar lligat a cap Activity. Tot i així cal sobrecarregar aquest mètode
     * per que retorni null.
     *
     * @param arg0 argument no utilitzat
     * @return null
     */
    @Override
    public final IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Invocada quan es crea el servei per primer cop, fa una sèrie
     * d'inicialitzacions. Són les següents inicialitzacions
     * <ul>
     * <li>estableix els tipus d'intents als quals dona resposta (veure
     * {@link TreeManagerService.Receptor})</li>
     * interfase d'usuari a mesura que vagi passant el temps</li>
     * <li>crea i posa en marxa el rellotge per cronometrar tasques</li>
     * </ul>
     */
    public final void onCreate() {
        Log.d(this.tag, "onCreate");

        final IntentFilter filter;
        filter = new IntentFilter();

        filter.addAction(ProjectsList.GIVE_ITEMS);
        filter.addAction(ProjectsList.GO_BACK);
        filter.addAction(ProjectsList.GO_FORWARD);
        filter.addAction(ProjectsList.SAVE_TREE);
        filter.addAction(ProjectsList.STOP_SERVICE);

        filter.addAction(CreateProject.CREATE_PROJECT);
        filter.addAction(CreateTask.CREATE_TASK);

        filter.addAction(ActivitiesList.ALPHA_COMPARATOR);
        filter.addAction(ActivitiesList.RECENT_COMPARATOR);
        filter.addAction(ActivitiesList.TYPE_COMPARATOR);

        filter.addAction(ActivitiesList.START);
        filter.addAction(ActivitiesList.STOP);
        filter.addAction(GenerateReport.GENERATE_REPORT);
        filter.addAction(TasksRunning.GIVE_TASKS_RUNNING);
        filter.addAction(ReportsList.GIVE_REPORTS);

        filter.addAction(ProjectsList.CHANGE_TASKS_RUNNING);
        filter.addAction(TasksRunning.STOP);
        filter.addAction(TasksRunning.PAUSE);
        filter.addAction(TasksRunning.RESUME);

        filter.addAction(ItemReportsListAdapter.DELETE_REPORT);
        filter.addAction(ProjectsList.DELETE_PROJECT);
        filter.addAction(IntervalsList.DELETE_INTERVAL);
        filter.addAction(EditProject.EDIT);
        filter.addAction(EditTask.EDIT);

        filter.addAction(Settings.GIVE_CONFIG);
        filter.addAction(Settings.SEND_NEW_CONFIG);
        filter.addAction(Settings.DELETE_ALL);

        filter.addAction(ProjectsList.ARE_TASKS_RUNNING);

        receptor = new TreeManagerService.Receptor();
        registerReceiver(receptor, filter);

        /**
         * Període de temps en segons dada quan s'actualitza la interfase d'usuari.
         * Es un paràmetre del constructor de {@link #updateIU}.
         */
        final int IUrefresh = 1000;
        this.updateIU = new Updater(this, IUrefresh,
                "TreeManagerService");

        // Escollir la opció desitjada d'entre ferArbreGran, llegirArbreArxiu i
        // ferArbrePetitBuit. Podríem primer fer l'arbre gran i després, quan
        // ja s'hagi desat, escollir la opció de llegir d'arxiu.

        this.readTree();
        this.readReportsFile();

        this.actualGroup = this.root;

        Log.d(this.tag, "l'arrel te " + this.root.getPGroupList().size() + " fills");

        this.configApp.setInstanceIDs();

        this.clock = Clock.instance(this.configApp.getDelaySeg());
        this.clock.start();

    }

    /**
     * En engegar el servei per primer cop, o després de ser parat i tornat a
     * engegar, enviem les dades de la llista de fills de la activitat actual.
     *
     * @param intent  veure la documentació online
     * @param flags   veure la documentació online
     * @param startId veure la documentació online
     * @return veure la documentació online
     */
    @Override
    public final int onStartCommand(Intent intent, int flags,
                                    int startId) {
        if ((flags & Service.START_FLAG_RETRY) == 0) {
            // es un restart, després d'acabar de manera anormal
            Log.d(this.tag, "onStartCommand repetit");
        } else {
            Log.d(this.tag, "onStartCommand per primer cop");
        }
        this.sendNewItems();
        // De l'exemple de la documentació de referència de la classe
        // Service de la web android developer: "We want this service
        // to continue running until it is explicitly stopped, so return
        // sticky".
        return Service.START_STICKY;
    }

    /**
     * Conté el mètode <code>onReceive</code> on es dona servei, o sigui, es
     * gestionen, les peticions provinents de les classes Activity que capturen
     * la interacció de l'usuari.
     * <p>
     * Concretament, de {@link ActivitiesList} rebem les peticions de
     * <ul>
     * <li><code>ENGEGA_CRONOMETRE</code> i <code>PARA_CRONOMETRE</code> d'una
     * tasca clicada, la qual s'inidica com el número d'ordre en la llista
     * d'activitats mostrades. Si la tasca no està sent ja cronometrada i es
     * demana, se li engega el cronòmetre. Si ho està sent i es demana, se li
     * para.</li>
     * <li><code>PUJA_NIVELL</code> i <code>BAIXA_NIVELL</code>, que fan que
     * s'actualitzi la activitat pare actual. Es responsabilitat del
     * sol·licitant llavors demanar que se li enviïn les noves dades a mostrar.
     * </li>
     * <li><code>DONAM_FILLS</code> demana que es construeixi una llista de les
     * dades dels fills de la activitat pare actual, ja sigui projecte o tasca.
     * Ho farà el mètode {@link TreeManagerService#sendNewItems()}, el qual
     * construeix aquesta llista i la posa com un "extra" a un Intent que té una
     * acció igual a TE_FILLS.</li>
     * <li><code>DESA_ARBRE</code> demana que s'escrigui l'arbre actual a
     * l'arxiu per defecte, privat de la aplicació, el nom del qual és a
     * {@link TreeManagerService#fileName}.</li>
     * <li>PARA_SERVEI</li> demana el que faríem si en Android es pogués
     * "sortir" de la aplicació: parar els handlers actualitzadors de la
     * interfase i el rellotge, parar el receptor d'intents, parar el cronòmetre
     * de les tasques que ho estan essent, i desar l'arbre a arxiu. Tot això ho
     * fa <code>paraServei</code>, que a més a més, fa un <code>stopSelf</code>
     * d'aquest servei.
     * </ul>
     * I de {@link IntervalsList} rebem <code>PUJA_NIVELL</code> i
     * <code>DONAM_FILLS</code> que tenen el mateix tractament.
     *
     * @author joans
     * @version 6 febrer 2012
     */
    private class Receptor extends BroadcastReceiver {
        /**
         * Nom de la classe per fer aparèixer als missatges de logging del
         * LogCat.
         *
         * @see Log
         */
        private final String tag = getClass().getCanonicalName();

        /**
         * Per comptes de fer una classe receptora per a cada tipus d'accio o
         * intent, els tracto tots aquí mateix, distingit-los per la seva
         * "action".
         *
         * @param context sol·licitant
         * @param intent  petició consistent en una acció (string) per identificar
         *                el tipus de petició, i paràmetres que l'acompanyen en
         *                forma d'extres de l'intent.
         */
        @Override
        public final void onReceive(Context context,
                                    Intent intent) {
            Log.d(this.tag, "onReceive");

            final String action = intent.getAction();
            Log.d(this.tag, "accio = " + action);

            assert action != null;
            switch (action) {
                case ActivitiesList.START:
                case ActivitiesList.STOP: {

                    final int posicio = intent.getIntExtra("pos", -1);

                    final Task selectedTask = (Task) ((Project) TreeManagerService.this.actualGroup).getPGroupList().toArray()[posicio];

                    if (action.equals(ActivitiesList.START)) {
                        if (!selectedTask.isRunning()) {
                            selectedTask.start();
                            Log.d(this.tag, "engego cronometre de "
                                    + selectedTask.getGName());

                            final int index = selectedTask.getgID();

                            boolean found = false;
                            int i = 0;

                            while (i < TreeManagerService.this.tasksRunning.size() && !found) {
                                if (TreeManagerService.this.tasksRunning.get(i).getgID() == index) {
                                    found = true;
                                } else {
                                    i++;
                                }
                            }

                            if (!found) {
                                TreeManagerService.this.tasksRunning.add(selectedTask);
                            }

                            TreeManagerService.this.updateIU.start(); // si ja ho esta no fa res
                        } else {
                            Log.w(this.tag, "intentat cronometrar la tasca "
                                    + selectedTask.getGName()
                                    + " que ja ho esta sent");
                        }
                    }

                    if (action.equals(ActivitiesList.STOP)) {
                        if (selectedTask.isRunning()) {
                            selectedTask.stop();
                            TreeManagerService.this.tasksRunning.remove(selectedTask);
                            if (TreeManagerService.this.tasksRunning.size() == 0) {
                                TreeManagerService.this.sendNewItems();
                                // es per dir a la IU (LlistaActivitatsActivity) que
                                // aquesta tasca ara ja te el cronometre parat.
                                // Sinó, si aquesta era la única tasca que es
                                // cronometrava, si directament paro
                                // l'actualitzador de la IU, quedara com que
                                // encara esta engegat per aquesta tasca i no es
                                // podrà tornar a engegar! I ara si que ja podem
                                // fer :
                                TreeManagerService.this.updateIU.stop();
                            }
                        } else {
                            Log.w(this.tag, "intentat parar cronometrae de la tasca "
                                    + selectedTask.getGName()
                                    + " que ja el te parat");
                        }
                    }
                    Log.d(this.tag, "Hi ha " + TreeManagerService.this.tasksRunning.size()
                            + " tasques cronometrant-se");

                    break;
                }
                case ProjectsList.SAVE_TREE:
                    TreeManagerService.this.writeTree();
                    TreeManagerService.this.writeConfig();
                    break;
                case ProjectsList.GIVE_ITEMS:
                    TreeManagerService.this.sendNewItems();
                    break;
                case ProjectsList.GO_BACK:
                    TreeManagerService.this.actualGroup = TreeManagerService.this.actualGroup.getGFather();
                    break;
                case ProjectsList.GO_FORWARD: {
                    // Anem a una les activitats filles => actualitzem
                    // l'activitat actual i enviem la llista d'activitats filles si
                    // és un projecte, o els intervals si és una tasca.
                    final int posicio = intent.getIntExtra("posicio", 0);
                    // El pare d'una activitat clicada nomes pot ser un projecte
                    // per que els intervals no son clicables (no gestionem aquest
                    // event). Ara, la activitat clicada tant pot ser un projecte
                    // com una tasca.

                    TreeManagerService.this.actualGroup = (Group) ((Project) TreeManagerService.this.actualGroup).getPGroupList().toArray()[posicio];

                    break;
                }
                case ProjectsList.STOP_SERVICE:
                    TreeManagerService.this.stopService(intent.getBooleanExtra("save", true));
                    break;
                case CreateProject.CREATE_PROJECT:
                    TreeManagerService.this.addProject(intent);
                    break;
                case CreateTask.CREATE_TASK:
                    TreeManagerService.this.addTask(intent);
                    break;
                case ActivitiesList.ALPHA_COMPARATOR:
                    Collections.sort(((Project) TreeManagerService.this.actualGroup).getPGroupList(), Group.AlphaComparator);
                    break;
                case ActivitiesList.RECENT_COMPARATOR:
                    Collections.sort(((Project) TreeManagerService.this.actualGroup).getPGroupList(), Group.RecentComparator);
                    break;
                case ActivitiesList.TYPE_COMPARATOR:
                    Collections.sort(((Project) TreeManagerService.this.actualGroup).getPGroupList(), Group.TypeComparator);
                    break;
                case GenerateReport.GENERATE_REPORT:
                    TreeManagerService.this.generateReport(intent);
                    break;
                case TasksRunning.GIVE_TASKS_RUNNING:
                    TreeManagerService.this.sendTasksRunning();
                    break;
                case ReportsList.GIVE_REPORTS:
                    TreeManagerService.this.sendReports();
                    break;
                case ItemReportsListAdapter.DELETE_REPORT:
                    TreeManagerService.this.deleteReport(intent);
                    break;
                case ProjectsList.CHANGE_TASKS_RUNNING:
                    TreeManagerService.this.tasks_running_open = !TreeManagerService.this.tasks_running_open;
                    break;
                case TasksRunning.STOP:
                    TreeManagerService.this.stopTasksRunning(intent.getIntExtra("id", -1), false);
                    break;
                case TasksRunning.PAUSE:
                    TreeManagerService.this.stopTasksRunning(intent.getIntExtra("id", -1), true);
                    break;
                case TasksRunning.RESUME:
                    TreeManagerService.this.resumeTasksRunning(intent.getIntExtra("id", -1));
                    break;
                case ProjectsList.DELETE_PROJECT:
                    TreeManagerService.this.deleteItem(intent);
                    break;
                case Settings.GIVE_CONFIG:
                    TreeManagerService.this.sendConfig();
                    break;
                case Settings.SEND_NEW_CONFIG:
                    TreeManagerService.this.setNewConfig(intent);
                    break;
                case ProjectsList.ARE_TASKS_RUNNING:
                    final Intent answer = new Intent(TreeManagerService.SEND_ARE_TASKS_RUNNING);
                    answer.putExtra("running", TreeManagerService.this.tasksRunning.isEmpty());
                    TreeManagerService.this.sendBroadcast(answer);
                    break;
                case IntervalsList.DELETE_INTERVAL:
                    TreeManagerService.this.deleteInterval(intent.getIntExtra("id", -1));
                    break;
                case EditProject.EDIT:
                case EditTask.EDIT:
                    TreeManagerService.this.editItem(intent);
                    break;
                case Settings.DELETE_ALL:
                    TreeManagerService.this.deleteALL();
                    break;
                default:
                    Log.d(this.tag, "accio desconeguda!");
                    break;
            }
            Log.d(this.tag, "final de onReceive");
        }
    }

    /**
     * Construeixi una llista de les dades dels fills de la activitat pare
     * actual, ja sigui projecte o tasca, per tal de ser mostrades (totes o
     * algunes d'aquestes dades) a la interfase d'usuari. Aquesta llista es posa
     * com a "extra" serialitzable d'un intent de nom TE_FILLS, del qual se'n fa
     * "broadcast".
     */
    private void sendNewItems() {
        final Intent resposta = new Intent(SEND_ITEMS);
        resposta.putExtra("activitat_pare_actual_es_arrel",
                (this.actualGroup == this.root));

        if (this.actualGroup.getClass().getName().endsWith("Project")) {

            final ArrayList<GroupDetails> groupDetailsList =
                    new ArrayList<GroupDetails>();

            for (final Group g : ((Project) this.actualGroup)
                    .getPGroupList()) {
                groupDetailsList.add(new GroupDetails(g));
            }

            groupDetailsList.add(new GroupDetails(this.actualGroup));

            resposta.putExtra("llista_dades_activitats", groupDetailsList);

        } else { // is task!
            final ArrayList<IntervalDetails> intervalDetailsList =
                    new ArrayList<IntervalDetails>();
            for (final Interval inter : ((Task) this.actualGroup)
                    .getTIntervals()) {
                intervalDetailsList.add(new IntervalDetails(inter));
            }

            resposta.putExtra("llista_dades_intervals", intervalDetailsList);
            resposta.putExtra("actual", new GroupDetails(this.actualGroup));
        }

        this.sendBroadcast(resposta);
        Log.d(this.tag, "enviat intent TE_FILLS d'activitat "
                + this.actualGroup.getClass().getName());
    }

    /**
     * Parar els handlers actualitzadors de la interfase i el rellotge, parar el
     * receptor d'intents, parar el cronòmetre de les tasques que ho estan
     * essent, desar l'arbre a arxiu i per últim fa un <code>stopSelf</code>
     * d'aquest servei, amb la qual cosa és semblant a tancar la aplicació.
     */
    private void stopService(final boolean save) {
        this.updateIU.stop();
        this.clock.stop();
        // el garbage collector ja els el·liminarà quan sigui, després de veure
        // que no es "reachable", com a conseqüència de que el servei és
        // eliminat també (espero).
        this.unregisterReceiver(this.receptor);
        // Això cal fer-ho per evitar un error en fer el darrer 'back'
        this.stopTasksRunning(-1, false);
        // Cal parar totes les tasques que s'estiguin cronometrant, per tal que
        // no es desin a l'arxiu com que si que ho estan sent i després, en
        // llegir
        // apareguin com cronometrant-se i generin problemes.
        if (save) {
            this.writeTree();
            this.writeConfig();
        }
        this.stopSelf();
        Log.d(this.tag, "servei desinstalat");
    }

    /**
     * Mètode de "forwarding" de <code>enviaFills</code> (per actualitzar
     * la interfase d'usuari) però només quan aquesta pot haver canviat,
     * és a dir, quan hi ha alguna tasca que s'està cronometrant.
     */

    private boolean tasks_running_open;

    public final void update() {
        Log.d(this.tag, "entro a actualitza de GestorArbreActivitats");
        if (this.tasksRunning.size() > 0) {
            this.sendNewItems();
            if (this.tasks_running_open) {
                this.sendTasksRunning();
            }
        }
    }

    /**
     * Para el cronòmetre de totes les tasques que ho estiguin sent,
     * al nivell que sigui de l'arbre.
     */
    private void stopTasksRunning(final int id, final boolean pause) {

        final ArrayList<Task> to_delete = new ArrayList<>();

        if (id == -1) {
            for (final Task t : this.tasksRunning) {
                if (t.isRunning()) {
                    t.stop();
                    if (!pause) {
                        to_delete.add(t);
                    }
                }
            }

            if (!pause) {
                for (final Task t : to_delete) {
                    this.tasksRunning.remove(t);
                }
            }
            this.sendTasksRunning();
        } else {
            boolean found = false;
            int index = 0;

            while (index < this.tasksRunning.size() && !found) {
                if (this.tasksRunning.get(index).getgID() == id) {
                    found = true;
                } else {
                    index++;
                }
            }

            if (found) {
                final Task t = this.tasksRunning.get(index);
                t.stop();
                if (!pause) {
                    this.tasksRunning.remove(index);
                }
            }

            this.sendTasksRunning();
        }
    }

    private void resumeTasksRunning(final int id) {

        if (id == -1) {
            for (final Task t : this.tasksRunning) {
                if (!t.isRunning()) {
                    t.start();
                }
            }

            this.sendTasksRunning();
        } else {
            boolean found = false;
            int index = 0;

            while (index < this.tasksRunning.size() && !found) {
                if (this.tasksRunning.get(index).getgID() == id) {
                    found = true;
                } else {
                    index++;
                }
            }

            if (found) {
                final Task t = this.tasksRunning.get(index);
                t.start();
            }

            this.sendTasksRunning();
        }
    }


    private void addProject(final Intent intent) {

        final String new_name = intent.getStringExtra("name");
        final String new_description = intent.getStringExtra("description");

        final Project to_add = new Project(new_name, new_description);

        final Project actual = (Project) this.actualGroup;
        actual.addGroup(to_add);
    }

    private void addTask(final Intent intent) {

        final String new_name = intent.getStringExtra("name");
        final String new_description = intent.getStringExtra("description");

        final Task to_add = new Task(new_name, new_description);

        final Project actual = (Project) this.actualGroup;
        actual.addGroup(to_add);
    }

    private ReportsArray reportsArray = new ReportsArray();
    public static final String fileNameReports = "reports.dat";
    public static final String SEND_REPORTS = "send_Reports";


    private void generateReport(final Intent intent) {

        try {
            final String publicDcimDirPath = intent.getStringExtra("publicDcimDirPath");
            final PackageInfo pInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            final String version = "TimeTracker v" + pInfo.versionName;

            final String name = intent.getStringExtra("name");
            final String date_start_compose = intent.getStringExtra("date1");
            final String date_end_compose = intent.getStringExtra("date2");

            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            assert date_start_compose != null;
            final Date date_start = sdf.parse(date_start_compose);
            assert date_end_compose != null;
            final Date date_end = sdf.parse(date_end_compose);


            final Period period = new Period(date_start, date_end);

            final Project project = (Project) this.actualGroup;

            final String format = intent.getStringExtra("format");
            final String type = intent.getStringExtra("type");

            final List<String> format_array = Arrays.asList(this.getResources().getStringArray(R.array.formatReports));
            final List<String> type_array = Arrays.asList(this.getResources().getStringArray(R.array.typeReports));

            final Intent resposta = new Intent(this, ShowReport.class);

            boolean created = false;

            assert type != null;

            if (type.equals(type_array.get(0))) {
                final RBasic report = new RBasic(project, period, version);
                report.writeReport();
                assert format != null;
                if (format.equals(format_array.get(0))) {
                    final String name_formatted = name + ".html";
                    final PrintWriter pw = new PrintWriter(new File(publicDcimDirPath, name_formatted));
                    report.buildReport(new FHTML(name, pw));
                    resposta.putExtra("name", name);
                    resposta.putExtra("path", publicDcimDirPath + "/" + name_formatted);
                    resposta.putExtra("type", "text/html");
                    this.reportsArray.addReport(name, publicDcimDirPath + "/" + name_formatted, "text/html");
                    created = true;

                } else if (format.equals(format_array.get(1))) {
                    final String name_formatted = name + ".txt";
                    final PrintWriter pw = new PrintWriter(new File(publicDcimDirPath, name_formatted));
                    report.buildReport(new FText(name, pw));
                    resposta.putExtra("name", name);
                    resposta.putExtra("path", publicDcimDirPath + "/" + name_formatted);
                    resposta.putExtra("type", "text/plain");
                    this.reportsArray.addReport(name, publicDcimDirPath + "/" + name_formatted, "text/plain");
                    created = true;

                }
            } else if (type.equals(type_array.get(1))) {
                final RDetailed report = new RDetailed(project, period, version);
                report.writeReport();
                assert format != null;
                if (format.equals(format_array.get(0))) {
                    final String name_formatted = name + ".html";
                    final PrintWriter pw = new PrintWriter(new File(publicDcimDirPath, name_formatted));
                    report.buildReport(new FHTML(name, pw));
                    resposta.putExtra("name", name);
                    resposta.putExtra("path", publicDcimDirPath + "/" + name_formatted);
                    resposta.putExtra("type", "text/html");
                    this.reportsArray.addReport(name, publicDcimDirPath + "/" + name_formatted, "text/html");
                    created = true;


                } else if (format_array.get(1).equals(format)) {
                    final String name_formatted = name + ".txt";
                    final PrintWriter pw = new PrintWriter(new File(publicDcimDirPath, name_formatted));
                    report.buildReport(new FText(name, pw));
                    resposta.putExtra("name", name);
                    resposta.putExtra("path", publicDcimDirPath + "/" + name_formatted);
                    resposta.putExtra("type", "text/plain");
                    this.reportsArray.addReport(name, publicDcimDirPath + "/" + name_formatted, "text/plain");
                    created = true;

                }

            }

            if (created) {
                resposta.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(resposta);
                this.writeReportsFile();
            }

        } catch (final ParseException | PackageManager.NameNotFoundException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public final void readReportsFile() {
        try {
            final FileInputStream fips = TreeManagerService.this.openFileInput(TreeManagerService.fileNameReports);
            final ObjectInputStream in = new ObjectInputStream(fips);
            TreeManagerService.this.reportsArray = (ReportsArray) in.readObject();
            in.close();
            Log.i(TreeManagerService.this.tag, "Reports llegit d'arxiu");
        } catch (final FileNotFoundException e) {
            Log.i(TreeManagerService.this.tag, "L'arxiu no es troba");
            TreeManagerService.this.reportsArray = new ReportsArray();
        } catch (final ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        Log.i("TAG", "llegir Reports File");
    }

    public final void writeReportsFile() {
        try {
            final FileOutputStream fops = TreeManagerService.this.openFileOutput(TreeManagerService.fileNameReports,
                    Context.MODE_PRIVATE);
            final ObjectOutputStream out = new ObjectOutputStream(fops);
            out.writeObject(TreeManagerService.this.reportsArray);
            out.close();
        } catch (final FileNotFoundException e) {
            Log.d(TreeManagerService.this.tag, "L'arxiu no es troba");
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        Log.i("TAG", "desa Reports File");
    }

    private void sendTasksRunning() {
        final Intent resposta = new Intent(SEND_TASKS_RUNNING);

        final ArrayList<GroupDetails> groupDetailsList =
                new ArrayList<GroupDetails>();

        for (final Task t : this.tasksRunning) {
            groupDetailsList.add(new GroupDetails(t));
        }

        resposta.putExtra("llista_tasques_running", groupDetailsList);

        this.sendBroadcast(resposta);

    }


    private void sendReports() {
        final Intent resposta = new Intent(SEND_REPORTS);

        final ArrayList<ReportsDetails> reportsDetails = this.reportsArray.getReportsArray();

        resposta.putExtra("llista_reports", reportsDetails);

        this.sendBroadcast(resposta);
    }

    private void deleteReport(final Intent intent) {

        final int id = intent.getIntExtra("id", -1);

        if (id != -1) {
            this.reportsArray.deleteReport(id);
            this.writeReportsFile();

            final File file = new File(Objects.requireNonNull(intent.getStringExtra("path")));
            if (file.exists()) {
                file.delete();
            }
        }
    }


    private void deleteItem(Intent intent) {

        int id = intent.getIntExtra("id", -1);
        boolean propagate = intent.getBooleanExtra("propagate", false);

        if (id != -1) {
            final Project actual = (Project) this.actualGroup;

            boolean found = false;
            int index = 0;

            while (index < actual.getPGroupList().size() && !found) {
                if (actual.getPGroupList().get(index).getgID() == id) {
                    found = true;
                } else {
                    index++;
                }
            }

            if (found) {
                if (propagate) {
                    int time = actual.getPGroupList().get(index).getGDuration();
                    actual.getPGroupList().get(index).propagateDurationChanges(time);
                }
                actual.getPGroupList().remove(index);
                this.sendNewItems();
            }
        }
    }

    private void deleteInterval(final int id) {

        if (id != -1) {
            final Task actual = (Task) this.actualGroup;

            boolean found = false;
            int index = 0;

            while (index < actual.getTIntervals().size() && !found) {
                if (actual.getTIntervals().get(index).getiID() == id) {
                    found = true;
                } else {
                    index++;
                }
            }

            if (found) {
                final int duration = actual.getTIntervals().get(index).getIDuration();
                actual.propagateDurationChanges(duration);

                if (index == 0) {
                    final Date oldStartDate = actual.getTIntervals().get(0).getIStartDate();
                    Date newStartDate = null;
                    if (index != actual.getTIntervals().size() - 1) {
                        newStartDate = actual.getTIntervals().get(index + 1).getIStartDate();
                    }
                    actual.propagateStartDate(oldStartDate, newStartDate);
                } else if (index == actual.getTIntervals().size() - 1) {
                    final Date oldEndDate = actual.getTIntervals().get(index).getIEndDate();
                    Date newEndDate = null;

                    if (actual.getTIntervals().size() > 1) {
                        newEndDate = actual.getTIntervals().get(index - 1).getIEndDate();
                    }
                    actual.propagateEndDate(oldEndDate, newEndDate);
                }

                actual.getTIntervals().remove(index);
                this.sendNewItems();
            }
        }
    }

    private void sendConfig() {
        final Intent intent = new Intent(TreeManagerService.SEND_CONFIG);
        intent.putExtra("config", this.configApp);
        this.sendBroadcast(intent);
    }

    private void setNewConfig(final Intent intent) {

        final int delay = intent.getIntExtra("delay", -1);

        if (delay != -1) {
            this.configApp.setDelaySeg(delay);
            this.writeConfig();

            Toasty.info(this, this.getString(R.string.changes), Toasty.LENGTH_LONG).show();
            System.exit(0);
        }
    }

    private void editItem(final Intent intent) {

        final int id = intent.getIntExtra("id", -1);

        if (id != -1) {

            final String name = intent.getStringExtra("name");
            final String description = intent.getStringExtra("description");

            boolean found = false;
            int index = 0;

            final Project project = (Project) this.actualGroup;

            while (index < project.getPGroupList().size() && !found) {
                if (project.getPGroupList().get(index).getgID() == id) {
                    found = true;
                } else {
                    index++;
                }
            }

            if (found) {
                project.getPGroupList().get(index).setgName(name);
                project.getPGroupList().get(index).setgDescription(description);
                this.sendNewItems();
            }

        }
    }


    private void deleteALL() {

        File file1 = new File(this.getFilesDir(), this.fileName);

        if (file1.exists()) {
            file1.delete();
        }

        file1 = new File(this.getFilesDir(), this.fileNameConfig);

        if (file1.exists()) {
            file1.delete();
        }

        System.exit(0);
    }
}
