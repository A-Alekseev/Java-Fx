package ru.gb.javafxapplication.common;

public class Message {
    private Command command;
    private String[] Parameters;

    public Message(Command command, String... parameters){
        this.command = command;
        this.Parameters = parameters;
    }

    public static Message fromString(String text)
    {
        Command command = Command.getCommand(text);
        String[] parameters = command.parse(text);
        return new Message(command, parameters);
    }

    public boolean isCommandEquals(Command command){
        return this.command == command;
    }


    public String getParameter(int index) {
        return Parameters[index];
    }

    @Override
    public String toString() {
        return command.getCommand() + " " + String.join(" ", Parameters);
    }

    public String[] getParameters() {
        return Parameters;
    }
}
