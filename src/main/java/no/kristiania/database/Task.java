package no.kristiania.database;

public class Task {
    private String name;
    private int id;
    private String ColorCode;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public String getColorCode() { return ColorCode; }

    public void setColorCode(String colorCode) { ColorCode = colorCode; }
}
