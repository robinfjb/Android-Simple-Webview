package robin.scaffold.lib.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class Parameters {
    private final Map<String, Map<Integer, Object>> paramHashValues = new LinkedHashMap<String, Map<Integer, Object>>();

    private int limit = -1;
    private int parameterCount = 0;
    private int parameterIndex = 0;

    public void addParameter(String key, Object value) throws IllegalStateException {
        if (key == null) {
            return;
        }
        if (!checkValuePrimitive(value)) {
            throw new IllegalArgumentException("Please use value which is primitive type like: String,Integer,Long and so on. But not Collection !");
        }

        parameterCount++;
        if (limit > -1 && parameterCount > limit) {
            // Processing this parameter will push us over the limit. ISE is
            // what Request.parseParts() uses for requests that are too big
            throw new IllegalStateException("parameters.maxCountFail: " + limit);
        }

        Map<Integer, Object> values = paramHashValues.get(key);
        if (values == null) {
            values = new LinkedHashMap<Integer, Object>(1);
            paramHashValues.put(key, values);
        }
        values.put(parameterIndex++, value == null ? "" : value);
    }

    private boolean checkValuePrimitive(Object value) {
        return (value == null || value instanceof String || value instanceof Integer
                || value instanceof Long || value instanceof Boolean || value instanceof Float
                || value instanceof Double || value instanceof Character || value instanceof Byte
                || value instanceof Short);
    }

    public String[] getParameterValues(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        return values.values().toArray(new String[values.size()]);
    }

    public String getParameter(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values != null) {
            if(values.size() == 0) {
                return "";
            }
            String value = values.values().iterator().next().toString();
            return (value != null && !"null".equals(value)) ? value : "";
        } else {
            return "";
        }
    }

    /*
     * If key does'nt exist will return null instead of ""
     */
    public String getParameterSpec(String name){
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values != null) {
            if(values.size() == 0) {
                return null;
            }
            String value = values.values().iterator().next().toString();
            return (value != null) ? value : null;
        } else {
            return null;
        }
    }

    public Set<String> getParameterNames() {
        return paramHashValues.keySet();
    }

    /**
     * Debug purpose
     */
    public String paramsAsString(ParamValueDerecator... dereactors) throws IOException {
        if (paramHashValues.isEmpty()) {
            return "";
        }

        ParamValueDerecator dereactor = null;
        if (dereactors != null && dereactors.length > 0) {
            dereactor = dereactors[0];
        }

        String[] paramArr = new String[parameterIndex];
        for (String name : paramHashValues.keySet()) {
            Map<Integer, Object> values = paramHashValues.get(name);
            for (Integer index : values.keySet()) {
                String value = values.get(index).toString();
                if(dereactor != null) {
                    value = dereactor.doInValue(value);
                }
                paramArr[index] = name + "=" + value;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String singleQuery : paramArr) {
            if (!singleQuery.isEmpty()) {
                sb.append(singleQuery).append("&");
            }
        }
        return sb.length() > 0 ? sb.subSequence(0, sb.length() - 1).toString() : "";
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCount(){
        return this.parameterCount;
    }

    public interface ParamValueDerecator {

        String doInValue(String value) throws IOException;

    }
}
