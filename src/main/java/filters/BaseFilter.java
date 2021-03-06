package filters;

import objects.JSONArray;
import objects.JSONFile;
import objects.JSONObject;
import objects.WrappedPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BaseFilter {

    <T> List<T> getAllOfType(JSONFile file, Class<T> clazz) {
        JSONObject baseObject = file.getObject();
        return accumulateType(baseObject, clazz);
    }

    <T> List<WrappedPrimitive<T>> getAllOfWrappedType(JSONFile file, Class<T> clazz) {
        ArrayList<WrappedPrimitive<T>> wrappedTypeList = new ArrayList<>();
        wrappedTypeList.addAll(accumulateWrappedTypeFromEntrySet(file.getObject().toMap().entrySet(), clazz));
        return wrappedTypeList;
    }

    @SuppressWarnings("unchecked")
    private <K, V, T> List<WrappedPrimitive<T>> accumulateWrappedTypeFromEntrySet(Set<Map.Entry<K, V>> entrySet, Class<T> clazz) {
        ArrayList<WrappedPrimitive<T>> wrappedTypeList = new ArrayList<>();
        entrySet.forEach(entry -> {
            if (isType(entry.getValue(), clazz)) {
                wrappedTypeList.add((WrappedPrimitive<T>) entry.getValue());
            }

            if (entry.getValue() instanceof JSONObject) {
                wrappedTypeList.addAll(accumulateWrappedTypeFromEntrySet(((JSONObject) entry.getValue()).toMap().entrySet(), clazz));
            } else if (entry.getValue() instanceof JSONArray) {
                wrappedTypeList.addAll(accumulateWrappedTypeFromJSONArray((JSONArray) entry.getValue(), clazz));
            }
        });
        return wrappedTypeList;
    }

    @SuppressWarnings("unchecked")
    private <T> List<WrappedPrimitive<T>> accumulateWrappedTypeFromJSONArray(JSONArray array, Class<T> clazz) {
        ArrayList<WrappedPrimitive<T>> wrappedTypeList = new ArrayList<>();
        array.toList().forEach(e -> {
            if (isType(e, clazz)) {
                wrappedTypeList.add((WrappedPrimitive<T>) e);
            }

            if (e instanceof JSONObject) {
                wrappedTypeList.addAll(accumulateWrappedTypeFromEntrySet(((JSONObject) e).toMap().entrySet(), clazz));
            } else if (e instanceof JSONArray) {
                wrappedTypeList.addAll(accumulateWrappedTypeFromJSONArray((JSONArray) e, clazz));
            }
        });
        return wrappedTypeList;
    }

    private <T> List<T> accumulateType(JSONObject toParse, Class<T> clazz) {
        ArrayList<T> typeList = new ArrayList<>();
        if (isType(toParse, clazz)) {
            typeList.add(clazz.cast(toParse));
        }
        typeList.addAll(accumulateFromEntrySet(toParse.toMap().entrySet(), clazz));
        return typeList;
    }

    private <K, V, T> List<T> accumulateFromEntrySet(Set<Map.Entry<K, V>> entrySet, Class<T> clazz) {
        ArrayList<T> typeList = new ArrayList<>();
        entrySet.forEach(entry -> {
            if (isType(entry.getValue(), clazz)) {
                typeList.add(clazz.cast(entry.getValue()));
            }

            if (entry.getValue() instanceof JSONObject) {
                typeList.addAll(accumulateFromEntrySet(((JSONObject) entry.getValue()).toMap().entrySet(), clazz));
            } else if (entry.getValue() instanceof JSONArray) {
                typeList.addAll(accumulateFromJSONArray((JSONArray) entry.getValue(), clazz));
            }
        });
        return typeList;
    }

    private <T> List<T> accumulateFromJSONArray(JSONArray jsonArray, Class<T> clazz) {
        ArrayList<T> typeList = new ArrayList<>();
        jsonArray.toList().forEach(e -> {
            if (isType(e, clazz)) {
                typeList.add(clazz.cast(e));
            }

            if (e instanceof JSONObject) {
                typeList.addAll(accumulateFromEntrySet(((JSONObject) e).toMap().entrySet(), clazz));
            } else if (e instanceof JSONArray) {
                typeList.addAll(accumulateFromJSONArray((JSONArray) e, clazz));
            }
        });
        return typeList;
    }

    private <T> boolean isType(Object object, Class<T> clazz) {
        if (object instanceof WrappedPrimitive) {
            if (clazz.isInstance(object)) {
                return clazz.isInstance(object);
            } else {
                return clazz.isInstance(((WrappedPrimitive) object).getValue());
            }
        }
        return clazz.isInstance(object);
    }
}
