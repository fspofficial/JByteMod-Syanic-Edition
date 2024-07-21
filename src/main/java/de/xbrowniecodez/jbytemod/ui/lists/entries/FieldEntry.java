package de.xbrowniecodez.jbytemod.ui.lists.entries;

import lombok.Getter;
import me.grax.jbytemod.utils.InstrUtils;
import me.grax.jbytemod.utils.TextUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@Getter
public class FieldEntry extends InstrEntry {

    private final ClassNode classNode;
    private final FieldNode fieldNode;
    private final String text;
    private final String easyText;

    public FieldEntry(ClassNode classNode, FieldNode fieldNode) {
        super(null, null);
        this.classNode = classNode;
        this.fieldNode = fieldNode;
        this.text = TextUtils
                .toHtml(InstrUtils.getDisplayAccess(fieldNode.access) + " " + InstrUtils.getDisplayType(fieldNode.desc, true) + " " + InstrUtils.getDisplayClassRed(fieldNode.name)
                        + " = " + (fieldNode.value instanceof String ? TextUtils.addTag("\"" + TextUtils.escape(String.valueOf(fieldNode.value)) + "\"", "font color=#559955")
                        : fieldNode.value != null ? String.valueOf(fieldNode.value) : TextUtils.toBold("null")));
        this.easyText = InstrUtils.getDisplayAccess(fieldNode.access) + " " + InstrUtils.getDisplayType(fieldNode.desc, false) + " "
                + InstrUtils.getDisplayClassEasy(fieldNode.name) + " = "
                + (fieldNode.value instanceof String ? "\"" + fieldNode.value + "\"" : String.valueOf(fieldNode.value));
    }

    @Override
    public String toString() {
        return text;
    }

    public String toEasyString() {
        return easyText;
    }
}
