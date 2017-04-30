package nz.james.senappsproximityapplication;

/**
 * Created by james on 30/04/2017.
 */

public class PlaceBundle {

    private InteractionBundle entryInteraction;
    private InteractionBundle exitInteraction;

    public PlaceBundle(){

    }

    public InteractionBundle getEntryInteraction() {
        return entryInteraction;
    }

    public void setEntryInteraction(InteractionBundle entryInteraction) {
        this.entryInteraction = entryInteraction;
    }

    public InteractionBundle getExitInteraction() {
        return exitInteraction;
    }

    public void setExitInteraction(InteractionBundle exitInteraction) {
        this.exitInteraction = exitInteraction;
    }
}
