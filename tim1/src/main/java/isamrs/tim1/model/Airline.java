package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Airlines")
public class Airline extends Service implements Serializable {

	private static final long serialVersionUID = 2322929543693920541L;
	
	public Airline() {
		super();
	}
}
