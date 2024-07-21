package de.xbrowniecodez.jbytemod.ui.lists.entries;

import lombok.Getter;
import me.grax.jbytemod.utils.InstrUtils;
import me.grax.jbytemod.utils.TextUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

@Getter
public class LVPEntry {
    private final ClassNode classNode;
    private final MethodNode methodNode;
    private final LocalVariableNode localVariableNode;
    private String text;

    public LVPEntry(ClassNode classNode, MethodNode methodNode, LocalVariableNode localVariableNode) {
        this.classNode = classNode;
        this.methodNode = methodNode;
        this.localVariableNode = localVariableNode;
        this.text = TextUtils.toHtml(TextUtils.toBold("#" + localVariableNode.index) + " ");
        if (localVariableNode.desc != null && !localVariableNode.desc.isEmpty()) {
            this.text += InstrUtils.getDisplayType(localVariableNode.desc, true) + " ";
        }
        this.text += TextUtils.addTag(TextUtils.escape(localVariableNode.name), "font color=#995555");
    }

    @Override
    public String toString() {
        return text;
    }

}
