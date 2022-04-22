package uk.ac.soton.comp1206.event;
/**
 * The Prompt exit listener is listening for the completion confirmation of any open prompt
 */
public interface PromptExitListener {
    /**
     * Triggers when a prompt is exited
     */
    public void onExit();
}
