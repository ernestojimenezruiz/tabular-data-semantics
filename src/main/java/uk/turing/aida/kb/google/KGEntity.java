package uk.turing.aida.kb.google;

import java.util.HashSet;
import java.util.Set;

public class KGEntity implements Comparable<KGEntity> {
	
	
	private String id;
	private String name;
	private String description; //very precise type
	private Set<String> types=new HashSet<String>(); //from schema.org
	
	private double score;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<String> getTypes() {
		return types;
	}
	
	
	protected String getTypesStr() {
		String types_str = "";
		
		for (String type: types)
			types_str += type + ";";
		return types_str;
	}
	
	public void setTypes(Set<String> types) {
		this.types = types;
	}
	
	
	public void addType(String type) {
		this.types.add(type);
	}
	
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(getId()).append("\n\t").append(getName()).append("\n\t").append(getDescription()).append("\n\t").append(getTypesStr()).append("\n\t").append(getScore());;
		
		return sb.toString();
		
	}
	
	
	
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof KGEntity))
			return false;
		
		KGEntity i =  (KGEntity)o;
		
		return equals(i);
		
	}
	
	
	public boolean equals(KGEntity m){
		
		if (!getId().equals(m.getId())){
			return false;
		}
		return true;
	}
	
	
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + getId().hashCode();
		  code = 50 * code + getName().hashCode();
		  return code;
	}
	
	
	public int compareTo(KGEntity e){
		
		if (equals(e))
			return 0;
		
		//Otherwise alphabetically
		//if (getName().compareTo(e.getName())>0)
		//Or hits
		if (getScore()>e.getScore())
			return -1;
		else
			return 1;
	}
	
	
	
	
	
	

}
