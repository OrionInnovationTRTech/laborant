package tr.com.orioninc.laborant.model;

public class CommandDTO {

    public String command;

    public CommandDTO() {
        this.command = null;
    }

    public CommandDTO(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
