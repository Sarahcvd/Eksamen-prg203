package no.kristiania.database;

public class Task {
    private String name;
    private Long id;
    private String ColorCode;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public String getColorCode() { return ColorCode; }

    public void setColorCode(String colorCode) { ColorCode = colorCode; }
}
