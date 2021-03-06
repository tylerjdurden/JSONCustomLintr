package objects;

import java.util.HashMap;
import java.util.Map;

public class JSONObject extends org.json.JSONObject implements WrappedObject {

    private final String originatingKey;
    private final WrappedObject parentObject;

    private final org.json.JSONObject clonedObject;

    JSONObject(String originatingKey,
                      WrappedObject parentObject,
                      org.json.JSONObject clone) {
        super(clone, org.json.JSONObject.getNames(clone));
        if (originatingKey == null && parentObject != null) {
            this.originatingKey = parentObject.getOriginatingKey();
        } else {
            this.originatingKey = originatingKey;
        }
        this.parentObject = parentObject;
        this.clonedObject = clone;
    }

    @Override
    public String getOriginatingKey() {
        return originatingKey;
    }

    @Override
    public WrappedObject getParentObject() {
        return parentObject;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public void parseAndReplaceWithWrappers() {
        clonedObject.toMap().entrySet().forEach(entry ->
            this.put(entry.getKey(),
                    WrappedObjectHelper.getWrappedObject(entry.getKey(), entry.getValue(), this))
        );
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> results = new HashMap<>();
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            Object value;
            if (entry.getValue() == null || NULL.equals(entry.getValue())) {
                value = null;
            } else if (entry.getValue() instanceof objects.JSONObject) {
                value = ((objects.JSONObject) entry.getValue()).toMap();
            } else if (entry.getValue() instanceof objects.JSONArray) {
                value = ((objects.JSONArray) entry.getValue()).toList();
            } else if (entry.getValue() instanceof org.json.JSONObject || entry.getValue() instanceof org.json.JSONArray) {
                value = WrappedObjectHelper.getWrappedObject(entry.getKey(), org.json.JSONObject.wrap(entry.getValue()), this);
            } else {
                value = WrappedObjectHelper.getWrappedObject(entry.getKey(), org.json.JSONObject.wrap(entry.getValue()), this);
            }
            results.put(entry.getKey(), value);
        }
        return results;
    }
}
