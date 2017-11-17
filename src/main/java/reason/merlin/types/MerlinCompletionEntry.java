package reason.merlin.types;

import com.fasterxml.jackson.databind.JsonNode;

public class MerlinCompletionEntry {
    public String name;
    public String kind /*value, variant, constructor, label, module, signature, type, method, #, exn, class*/;
    public String desc;
    public String info;

    public MerlinCompletionEntry(JsonNode element) {
        //{"name":"append","kind":"Value","desc":"list 'a => list 'a => list 'a","info":""}
        this.name = element.get("name").asText();
        this.kind = element.get("kind").asText();
        this.desc = element.get("desc").asText();
        this.info = element.get("info").asText();
    }

    @Override
    public String toString() {
        return "MerlinCompletionEntry (" + kind + ") " + name + ": " + desc + ", info='" + info + "'";
    }
}
