package net.elpuig.daw2.javabeans;

public class Alumno {
    private int id;
    private String curso;
    private String nombre;

    public Alumno(int id, String curso, String nombre) {
        this.id = id;
        this.curso = curso;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getCurso() { return curso; }
    public String getNombre() { return nombre; }
}
