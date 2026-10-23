/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

import com.fasterxml.jackson.annotation.JsonSetter;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class GeneralSettings {
    private String language = "en_US";
    private UpdateSettings updates = new UpdateSettings();

    public String language() {
        return language;
    }

    /**
     * @return whether the server is told about new versions and whether they are downloaded
     */
    public UpdateSettings updates() {
        return updates;
    }

    /**
     * Reads the update switch of 1.x configurations. It becomes the start value of {@link UpdateSettings#notice()} and
     * is not written again.
     *
     * @param updateCheck whether the old configuration looked for new versions
     */
    @JsonSetter("updateCheck")
    private void legacyUpdateCheck(boolean updateCheck) {
        updates.notice(updateCheck);
    }
}
