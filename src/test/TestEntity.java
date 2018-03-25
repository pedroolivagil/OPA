package test;

import com.olivadevelop.persistence.annotations.Entity;
import com.olivadevelop.persistence.annotations.Id;
import com.olivadevelop.persistence.entities.BasicEntity;

@Entity(table = "locale")
public class TestEntity extends BasicEntity {

    @Id
    private Integer id;
    private String name;
    private String apellido;
    private Double precio;
    private boolean bool;

    public TestEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", apellido='" + apellido + '\'' +
                ", precio=" + precio +
                ", bool=" + bool +
                '}';
    }
}
