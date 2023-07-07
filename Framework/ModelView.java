package etu1816.framework;

import java.util.HashMap;
import com.google.gson.Gson;

public class ModelView {
    String view;
    HashMap<String, Object> data;
    HashMap<String, String> session;
    boolean json;
    Gson gson;
    boolean api;

    public void addItem(String key, Object value) {
        this.data.put(key, value);
    }

    public ModelView() {
        data = new HashMap<>();
        session = new HashMap<>();
        json = false;
        gson = new Gson();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public HashMap<String, String> getSession() {
        return session;
    }

    public void setSession(HashMap<String, String> session) {
        this.session = session;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public boolean isApi() {
        return api;
    }

    public void setApi(boolean api) {
        this.api = api;
    }
    public String toJson() {
        return !isApi() ? gson.toJson(data) : gson.toJson(data.get("objectDataResultFramework"));
    }
}
