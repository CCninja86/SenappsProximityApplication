package nz.james.senappsproximityapplication;

/**
 * Created by james on 18/03/2017.
 */

public class Interaction {

    private String ID;
    private String InteractionName;
    private String InteractionDescription;
    private String TriggerID;
    private String ActionType;
    private String ContentID;
    private String NotificationMessage;
    private String user;

    public Interaction(){

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInteractionName() {
        return InteractionName;
    }

    public void setInteractionName(String interactionName) {
        InteractionName = interactionName;
    }

    public String getInteractionDescription() {
        return InteractionDescription;
    }

    public void setInteractionDescription(String interactionDescription) {
        InteractionDescription = interactionDescription;
    }

    public String getTriggerID() {
        return TriggerID;
    }

    public void setTriggerID(String triggerID) {
        TriggerID = triggerID;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    public String getContentID() {
        return ContentID;
    }

    public void getContentID(String contentID) {
        ContentID = contentID;
    }

    public String getNotificationMessage() {
        return NotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        NotificationMessage = notificationMessage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
