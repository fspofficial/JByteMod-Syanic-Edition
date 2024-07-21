package de.xbrowniecodez.jbytemod.ui.lists.entries;

import lombok.Getter;
import me.grax.jbytemod.utils.InstrUtils;
import me.grax.jbytemod.utils.TextUtils;
import me.grax.jbytemod.utils.asm.Hints;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

@Getter
public class InstrEntry {
    private final MethodNode methodNode;
    private final AbstractInsnNode insnNode;

    public InstrEntry(MethodNode methodNode, AbstractInsnNode insnNode) {
        this.methodNode = methodNode;
        this.insnNode = insnNode;
    }

    @Override
    public String toString() {
        return TextUtils.toHtml(InstrUtils.toString(insnNode));
    }

    public String toEasyString() {
        return InstrUtils.toEasyString(insnNode);
    }

    public String getHint() {
        if (insnNode != null && insnNode.getOpcode() >= 0) {
            return Hints.hints[insnNode.getOpcode()];
        }
        return null;
    }
}
