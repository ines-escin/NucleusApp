package io.github.inesescin.nucleus.callback;

import java.util.Map;

import io.github.inesescin.nucleus.models.Nucleus;

/**
 * Created by jal3 on 28/03/2016.
 */
public interface EcopointsCallback {
    void onEcopointsReceived(Map<String, Nucleus> ecopoints);
}
