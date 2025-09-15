package com.warehouse.jsonModel;

public class Root {
    private Main main;   // ← внутри него лежит temp
    private String name; // ← город

    public Main getMain() { return main; }
    public String getName() { return name; }
}
