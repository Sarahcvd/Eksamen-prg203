package no.kristiania.DAO;

public class Task {
    private String name;
    private int id;
    private String StatusColorCode;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public String getStatusColorCode() { return StatusColorCode; }

    public void setStatusColorCode(String statusColorCode) { StatusColorCode = statusColorCode; }
}
