package isamrs.tim1.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RentACars")
public class RentACar extends Service implements Serializable{
	// do mapping
	private Set<BranchOffice> branchOffices;
	
	// add vehicles
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7086320997392327254L;
}
