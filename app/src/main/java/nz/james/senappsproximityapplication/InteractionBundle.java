package nz.james.senappsproximityapplication;

/**
 * Created by james on 24/04/2017.
 */

public class InteractionBundle {

    private Interaction interaction;
    private Trigger trigger;
    private Content content;

    public InteractionBundle(){

    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
