package com.reason.merlin.types;

public class MerlinCompletionEntry {
    public String name;
    public String kind /*value, variant, constructor, label, module, signature, type, method, #, exn, class*/;
    public String desc;
    public String info;

    @Override
    public String toString() {
        return "MerlinCompletionEntry (" + kind + ") " + name + ": " + desc + ", info='" + info + "'";
    }
}
