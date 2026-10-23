package de.eldoria.pickmeup.config;

/**
 * Whether the server is told about new PickMeUp versions and whether they are downloaded.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class UpdateSettings {
    private boolean notice = true;
    private boolean autoUpdate = false;
    private boolean askForAutoUpdate = true;

    /**
     * @return whether the console and admins hear about a new version
     */
    public boolean notice() {
        return notice;
    }

    /**
     * @param notice whether the console and admins hear about a new version
     */
    public void notice(boolean notice) {
        this.notice = notice;
    }

    /**
     * @return whether a new stable release is downloaded, to take effect on the next start
     */
    public boolean autoUpdate() {
        return autoUpdate;
    }

    /**
     * @param autoUpdate whether a new stable release is downloaded, to take effect on the next start
     */
    public void autoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * @return whether admins are asked in chat to switch auto update on
     */
    public boolean askForAutoUpdate() {
        return askForAutoUpdate;
    }

    /**
     * @param askForAutoUpdate whether admins are asked in chat to switch auto update on
     */
    public void askForAutoUpdate(boolean askForAutoUpdate) {
        this.askForAutoUpdate = askForAutoUpdate;
    }
}
