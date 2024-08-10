package modelo.entidades;

import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Entity
public class CategoriaEgreso extends Categoria implements Serializable {


    public CategoriaEgreso() {
    }

    public CategoriaEgreso( String nombre, Integer ID) {
        super(nombre, ID);
    }
}
