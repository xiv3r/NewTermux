package com.newtermux.features;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

public class SshProfile {
    public String id;
    public String nickname;
    public String host;
    public int port;
    public String username;
    public String keyPath; // empty = password auth (user types it)

    public SshProfile() {
        id = UUID.randomUUID().toString();
        port = 22;
        keyPath = "";
    }

    public String buildCommand() {
        StringBuilder cmd = new StringBuilder("ssh");
        cmd.append(" -o StrictHostKeyChecking=accept-new");
        if (port != 22) cmd.append(" -p ").append(port);
        if (keyPath != null && !keyPath.isEmpty()) cmd.append(" -i ").append(keyPath);
        cmd.append(" ").append(username).append("@").append(host);
        return cmd.toString();
    }

    public String displayLabel() {
        String base = username + "@" + host;
        return port != 22 ? base + ":" + port : base;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("nickname", nickname != null ? nickname : "");
        o.put("host", host != null ? host : "");
        o.put("port", port);
        o.put("username", username != null ? username : "");
        o.put("keyPath", keyPath != null ? keyPath : "");
        return o;
    }

    public static SshProfile fromJson(JSONObject o) throws JSONException {
        SshProfile p = new SshProfile();
        p.id = o.optString("id", UUID.randomUUID().toString());
        p.nickname = o.optString("nickname", "");
        p.host = o.optString("host", "");
        p.port = o.optInt("port", 22);
        p.username = o.optString("username", "");
        p.keyPath = o.optString("keyPath", "");
        return p;
    }
}
