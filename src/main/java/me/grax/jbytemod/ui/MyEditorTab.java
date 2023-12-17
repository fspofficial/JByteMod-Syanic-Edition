package me.grax.jbytemod.ui;

import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.discord.Discord;
import me.grax.jbytemod.ui.graph.ControlFlowPanel;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class MyEditorTab extends JPanel {
    private static String analysisText = JByteMod.res.getResource("analysis");
    private MyCodeEditor codeEditor;
    private JLabel label;
    private JPanel code, info;
    private DecompilerTab decompiler;
    private ControlFlowPanel analysis;
    private JPanel center;
    private JButton decompilerBtn;
    private JButton analysisBtn;
    private JButton codeBtn;
    private boolean classSelected = false;

    public MyEditorTab(JByteMod jbm) {
        setLayout(new BorderLayout());
        this.center = new JPanel();
        center.setLayout(new GridLayout());
        this.label = new JLabel("JByte Mod");

        this.codeEditor = new MyCodeEditor(jbm, label);
        jbm.setCodeList(codeEditor.getEditor());
        this.code = withBorder(label, codeEditor);

        InfoPanel sp = new InfoPanel(jbm);
        jbm.setSP(sp);

        this.info = this.withBorder(new JLabel(JByteMod.res.getResource("settings")), sp);

        this.decompiler = new DecompilerTab(jbm);
        this.decompiler.setName("decompiler");

        jbm.setCFP(this.analysis = new ControlFlowPanel(jbm));
        this.analysis.setName("analysis");

        center.add(code);

        JPanel selector = new JPanel();
        codeBtn = new JButton("Code");
        codeBtn.setSelected(true);
        codeBtn.addActionListener(e -> showPanel(code));
        JButton infoBtn = new JButton("Info");
        infoBtn.addActionListener(e -> showPanel(info));
        decompilerBtn = new JButton("Decompiler");
        decompilerBtn.addActionListener(e -> {
            showPanel(decompiler);
            decompiler.decompile(jbm.getCurrentNode(), jbm.getCurrentMethod(), false);
        });
        analysisBtn = new JButton(analysisText);
        analysisBtn.addActionListener(e -> {
            showPanel(analysis);
            if (!classSelected) {
                analysis.generateList();
            } else {
                analysis.clear();
            }
        });

        selector.add(codeBtn);
        selector.add(infoBtn);
        selector.add(decompilerBtn);
        selector.add(analysisBtn);
        selector.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(center, BorderLayout.CENTER);
        this.add(selector, BorderLayout.PAGE_END);
    }

    public JButton getCodeBtn() {
        return codeBtn;
    }

    private void showPanel(Component panel) {
        if (center.getComponent(0) != panel) {
            center.removeAll();
            center.add(panel);
            center.revalidate();
            repaint();
        }
    }

    private JPanel withBorder(JLabel label, Component c) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        JPanel lpad = new JPanel();
        lpad.setBorder(new EmptyBorder(1, 5, 0, 5));
        lpad.setLayout(new GridLayout());
        lpad.add(label);
        panel.add(lpad, BorderLayout.NORTH);
        JScrollPane scp = new JScrollPane(c);
        scp.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scp, BorderLayout.CENTER);
        return panel;
    }

    public void selectClass(ClassNode cn) {
        JByteMod.instance.getDiscord().updatePresence("Working on " + JByteMod.lastEditFile, "Editing " + cn.name);

        String selectedComponentName = center.getComponent(0).getName();
        if(Objects.nonNull(selectedComponentName)) {
            if(selectedComponentName.equals("decompiler"))
                decompiler.decompile(cn, null, false);
            else if (selectedComponentName.equals("analysis"))
                analysis.clear();
        }

        this.classSelected = true;
    }

    public void selectMethod(ClassNode cn, MethodNode mn) {
        JByteMod.instance.getDiscord().updatePresence("Working on " + JByteMod.lastEditFile, "Editing " + cn.name + "." + mn.name);

        String selectedComponentName = center.getComponent(0).getName();
        if(Objects.nonNull(selectedComponentName)) {
            if (selectedComponentName.equals("decompiler"))
                decompiler.decompile(cn, mn, false);
            else if (selectedComponentName.equals("analysis"))
                analysis.generateList();
        }
        this.classSelected = false;
    }
}
