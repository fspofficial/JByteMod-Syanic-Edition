package me.grax.jbytemod;

import com.sun.tools.attach.VirtualMachine;
import de.xbrowniecodez.jbytemod.securitymanager.CustomSecurityManager;
import de.xbrowniecodez.jbytemod.utils.BytecodeUtils;
import de.xbrowniecodez.jbytemod.utils.Utils;
import de.xbrowniecodez.jbytemod.utils.update.UpdateChecker;
import de.xbrowniecodez.jbytemod.utils.update.objects.Version;
import lombok.Getter;
import me.grax.jbytemod.discord.Discord;
import me.grax.jbytemod.logging.Logging;
import me.grax.jbytemod.plugin.Plugin;
import me.grax.jbytemod.plugin.PluginManager;
import me.grax.jbytemod.res.LanguageRes;
import me.grax.jbytemod.res.Options;
import me.grax.jbytemod.ui.*;
import me.grax.jbytemod.ui.graph.ControlFlowPanel;
import de.xbrowniecodez.jbytemod.ui.lists.LVPList;
import me.grax.jbytemod.ui.lists.MyCodeList;
import de.xbrowniecodez.jbytemod.ui.lists.SearchList;
import de.xbrowniecodez.jbytemod.ui.lists.TCBList;
import me.grax.jbytemod.ui.tree.SortedTreeNode;
import me.grax.jbytemod.utils.ErrorDisplay;
import me.grax.jbytemod.utils.FileUtils;
import me.grax.jbytemod.utils.gui.LookUtils;
import me.grax.jbytemod.utils.task.AttachTask;
import me.grax.jbytemod.utils.task.RetransformTask;
import me.grax.jbytemod.utils.task.SaveTask;
import me.lpk.util.OpUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class JByteMod extends JFrame {
	public static final Version version = new Version(Utils.readPropertiesFile().getProperty("version"));
    private static final String jbytemod = "JByteMod Remastered v" + version;
    
    public static File workingDir = new File(".");
    public static String configPath = "jbyte-remastered.cfg";
    public static Logging LOGGER;
    public static LanguageRes res;
    public static Options ops;
    public static String lastEditFile = "";
    public static HashMap<ClassNode, MethodNode> lastSelectedTreeEntries = new LinkedHashMap<>();
    public static JByteMod instance;
    public static Color border;
    private static boolean lafInit;
    private static JarArchive file;
    private static Instrumentation agentInstrumentation;

    static {
        try {
            System.loadLibrary("attach");
        } catch (Throwable ex) {
        }
    }

    private JPanel contentPane;
    @Getter
    private ClassTree jarTree;
    private MyCodeList clist;
    private PageEndPanel pp;
    private SearchList slist;
    @Getter
    private DecompilerPanel dp;
    private TCBList tcblist;
    @Getter
    private MyTabbedPane tabbedPane;
    private InfoPanel sp;
    private LVPList lvplist;
    private ControlFlowPanel cfp;
    @Getter
    private MyMenuBar myMenuBar;
    @Getter
    private ClassNode currentNode;
    @Getter
    private MethodNode currentMethod;
    @Getter
    private PluginManager pluginManager;
    @Getter
    private File filePath;
    @Getter
    private final Discord discord = new Discord("1184572566795468881");

    /**
     * Create the frame.
     * @throws Exception 
     */
    public JByteMod(boolean agent) throws Exception {
        discord.init();
        initialize();
        initializeComponents(agent);
    }

    public static void initialize() {
        LOGGER = new Logging();
        res = new LanguageRes();
        ops = new Options();

        try {
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Throwable t) {
            LOGGER.err("Failed to set encoding to UTF-8 (" + t.getMessage() + ")");
        }
    }

    private void initializeComponents(boolean agent) throws Exception {
        new UpdateChecker();
        new CustomSecurityManager();
        initializeFrame(agent);
    }

    private void initializeFrame(boolean agent) {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                handleWindowClosing(agent);
            }
        });

        border = UIManager.getColor("nimbusBorder");
        if (border == null) {
            border = new Color(146, 151, 161);
        }

        setBounds(100, 100, 1280, 720);
        setTitle(jbytemod);
        setJMenuBar(myMenuBar = new MyMenuBar(this, agent));
        jarTree = new ClassTree(this);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);
        setTCBList(new TCBList());
        setLVPList(new LVPList());
        createSplitPane();
        contentPane.add(pp = new PageEndPanel(), BorderLayout.PAGE_END);
        contentPane.add(new MyToolBar(this), BorderLayout.PAGE_START);

        if (file != null) {
            refreshTree();
        }
    }

    private void createSplitPane() {
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(null);
        borderPanel.setLayout(new GridLayout());
        JSplitPane splitPane = new MySplitPane(this, jarTree);
        JPanel b2 = new JPanel();
        b2.setBorder(new EmptyBorder(5, 0, 5, 0));
        b2.setLayout(new GridLayout());
        b2.add(splitPane);
        borderPanel.add(b2);
        contentPane.add(borderPanel, BorderLayout.CENTER);
    }

    private void handleWindowClosing(boolean agent) {
        if (JOptionPane.showConfirmDialog(JByteMod.this, res.getResource("exit_warn"), res.getResource("is_sure"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (agent) {
                dispose();
            } else {
                instance.discord.getDiscordRPC().Discord_Shutdown();
                Runtime.getRuntime().exit(0);
            }
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        CommandLine cmd = parseCommandLine(args);
        if (cmd.hasOption("help")) {
            printHelpAndExit();
        }

        configureWorkingDirectory(cmd);
        configureConfigPath(cmd);
        initialize();

        EventQueue.invokeLater(() -> {
            try {
                initializeLookAndFeel();

                JByteMod frame = new JByteMod(false);
                instance = frame;
                frame.setVisible(true);

                loadFileIfNeeded(cmd, frame);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static CommandLine parseCommandLine(String[] args) {
        org.apache.commons.cli.Options options = buildCommandLineOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while parsing the commandline ");
        }
    }

    private static org.apache.commons.cli.Options buildCommandLineOptions() {
        org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption("f", "file", true, "File to open");
        options.addOption("d", "dir", true, "Working directory");
        options.addOption("c", "config", true, "Config file name");
        options.addOption("?", "help", false, "Prints this help");
        return options;
    }

    private static void printHelpAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(jbytemod, buildCommandLineOptions());
        System.exit(0);
    }

    private static void configureWorkingDirectory(CommandLine cmd) {
        if (cmd.hasOption("d")) {
            workingDir = new File(cmd.getOptionValue("d"));
            if (!(workingDir.exists() && workingDir.isDirectory())) {
                printHelpAndExit();
            }
            JByteMod.LOGGER.err("Specified working dir set");
        }
    }

    private static void configureConfigPath(CommandLine cmd) {
        if (cmd.hasOption("c")) {
            configPath = cmd.getOptionValue("c");
        }
    }

    private static void initializeLookAndFeel() {
        if (!lafInit) {
            LookUtils.setTheme();
            lafInit = true;
        }
    }

    private static void loadFileIfNeeded(CommandLine cmd, JByteMod frame) {
        if (cmd.hasOption("f")) {
            File input = new File(cmd.getOptionValue("f"));
            if (FileUtils.exists(input) && FileUtils.isType(input, ".jar", ".class")) {
                frame.loadFile(input);
                JByteMod.LOGGER.log("Specified file loaded");
            } else {
                JByteMod.LOGGER.err("Specified file not found");
            }
        }
    }


    public void applyChangesAgent() {
        if (agentInstrumentation == null) {
            throw new RuntimeException();
        }
        new RetransformTask(this, agentInstrumentation, file).execute();
    }

    public void attachTo(VirtualMachine vm) throws Exception {
        new AttachTask(this, vm).execute();
    }

    public ControlFlowPanel getCFP() {
        return this.cfp;
    }

    public void setCFP(ControlFlowPanel cfp) {
        this.cfp = cfp;
    }

    public MyCodeList getCodeList() {
        return clist;
    }

    public void setCodeList(MyCodeList list) {
        this.clist = list;
    }

    public JarArchive getFile() {
        return file;
    }

    public LVPList getLVPList() {
        return lvplist;
    }

    private void setLVPList(LVPList lvp) {
        this.lvplist = lvp;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PageEndPanel getPP() {
        return pp;
    }

    public SearchList getSearchList() {
        return slist;
    }

    public void setTabbedPane(MyTabbedPane tp) {
        this.tabbedPane = tp;
    }

    public TCBList getTCBList() {
        return tcblist;
    }

    public void setTCBList(TCBList tcb) {
        this.tcblist = tcb;
    }

    /**
     * Load .jar or .class file
     */
    public void loadFile(File input) {
        this.filePath = input;
        String ap = input.getAbsolutePath();

        try {
            if (ap.endsWith(".jar")) {
                loadJarFile(input);
            } else if (ap.endsWith(".class")) {
                loadClassFile(input);
            } else {
                displayJarWarning();
            }

            notifyPlugins();
        } catch (Throwable e) {
            new ErrorDisplay(e);
        }
    }

    private void loadJarFile(File input) {
        file = new JarArchive(this, input);
        setTitleSuffix(input.getName());
    }

    private void loadClassFile(File input) throws Exception {
        file = new JarArchive(BytecodeUtils.getClassNodeFromBytes(Files.readAllBytes(input.toPath())));
        setTitleSuffix(input.getName());
        refreshTree();
    }

    private void displayJarWarning() {
        new ErrorDisplay(new UnsupportedOperationException(res.getResource("jar_warn")));
    }

    private void notifyPlugins() {
        for (Plugin p : pluginManager.getPlugins()) {
            p.loadFile(file.getClasses());
        }
    }


    public void refreshAgentClasses() {
        if (agentInstrumentation == null) {
            throw new RuntimeException();
        }
        this.refreshTree();
    }

    public void refreshTree() {
        LOGGER.log("Building tree..");
        this.jarTree.refreshTree(file);
    }

    public void saveFile(File output) {
        try {
            new SaveTask(this, output, file).execute();
        } catch (Throwable t) {
            new ErrorDisplay(t);
        }
    }

    public void selectClass(ClassNode cn) {
        if (ops.get("select_code_tab").getBoolean()) {
            tabbedPane.setSelectedIndex(0);
        }
        this.currentNode = cn;
        this.currentMethod = null;
        sp.selectClass(cn);
        clist.loadFields(cn);
        tabbedPane.selectClass(cn);
        lastSelectedTreeEntries.put(cn, null);
        if (lastSelectedTreeEntries.size() > 5) {
            lastSelectedTreeEntries.remove(lastSelectedTreeEntries.keySet().iterator().next());
        }
    }

    private boolean selectEntry(MethodNode mn, DefaultTreeModel tm, SortedTreeNode node) {
        for (int i = 0; i < tm.getChildCount(node); i++) {
            SortedTreeNode child = (SortedTreeNode) tm.getChild(node, i);
            if (child.getMn() != null && child.getMn().equals(mn)) {
                TreePath tp = new TreePath(tm.getPathToRoot(child));
                jarTree.setSelectionPath(tp);
                jarTree.scrollPathToVisible(tp);
                return true;
            }
            if (!child.isLeaf()) {
                if (selectEntry(mn, tm, child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void selectMethod(ClassNode cn, MethodNode mn) {
        if (ops.get("select_code_tab").getBoolean()) {
            tabbedPane.setSelectedIndex(0);
        }
        OpUtils.clearLabelCache();
        this.currentNode = cn;
        this.currentMethod = mn;
        sp.selectMethod(cn, mn);
        if (!clist.loadInstructions(mn)) {
            clist.setSelectedIndex(-1);
        }
        tcblist.addNodes(cn, mn);
        lvplist.addNodes(cn, mn);
        cfp.setNode(mn);
        dp.setText("");
        tabbedPane.selectMethod(cn, mn);
        lastSelectedTreeEntries.put(cn, mn);
        if (lastSelectedTreeEntries.size() > 5) {
            lastSelectedTreeEntries.remove(lastSelectedTreeEntries.keySet().iterator().next());
        }
    }

    public void setDP(DecompilerPanel dp) {
        this.dp = dp;
    }

    public void setSearchlist(SearchList searchList) {
        this.slist = searchList;
    }

    public void setSP(InfoPanel sp) {
        this.sp = sp;
    }

    private void setTitleSuffix(String suffix) {
        this.setTitle(jbytemod + " - " + suffix);
    }

    @Override
    public void setVisible(boolean b) {
        this.setPluginManager(new PluginManager(this));
        this.myMenuBar.addPluginMenu(pluginManager.getPlugins());
        super.setVisible(b);
    }

    public void treeSelection(ClassNode cn, MethodNode mn) {
        //selection may take some time
        new Thread(() -> {
            DefaultTreeModel tm = (DefaultTreeModel) jarTree.getModel();
            if (this.selectEntry(mn, tm, (SortedTreeNode) tm.getRoot())) {
                jarTree.repaint();
            }
        }).start();
    }

}
