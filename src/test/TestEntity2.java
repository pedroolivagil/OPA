package test;

import com.olivadevelop.persistence.annotations.*;
import com.olivadevelop.persistence.entities.BasicEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Entity(table = "test")
public class TestEntity2 extends BasicEntity {

    @Id
    private Integer id;

    @Persistence(column = "name")
    private String name;
    private String apellido;
    private Double precio;
    private boolean bool;

    public TestEntity2() {
    }

    public TestEntity2(JSONObject json) throws JSONException {
        super(json);
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
        return "TestEntity2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", apellido='" + apellido + '\'' +
                ", precio=" + precio +
                ", bool=" + bool +
                '}';
    }
}
