package de.klaro.reformcloud2.executor.api.common.patch.basic;

import de.klaro.reformcloud2.executor.api.common.patch.Patch;

public final class DefaultPatch implements Patch {

    public DefaultPatch(String url, DefaultPatchNote note) {
        this.url = url;
        this.note = note;
    }

    private String url;

    private DefaultPatchNote note;

    @Override
    public String fileName() {
        return url;
    }

    @Override
    public DefaultPatchNote patchNote() {
        return note;
    }
}
