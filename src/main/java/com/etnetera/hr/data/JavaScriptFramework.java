package com.etnetera.hr.data;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 * 
 * @author Etnetera
 *
 */
@Entity
public class JavaScriptFramework {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	@Column()
	private String version;

	@Column()
	private LocalDate deprecationDate;
	
	@Column()
	private String hypeLevel;

	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setVersion(String input) {
        int occurence = countMatches(input, '.');
        if (occurence == 0)
            input += ".0.0";
        else if (occurence == 1)
            input += ".0";
        version = input;
    }
    public int countMatches(final CharSequence str, final char ch) {
        if (null == str) {
            return 0;
        }
        int count = 0;
        char[] arr = ((String) str).toCharArray();
        for (char c: arr) {
            if (c == ch)
                count++;
        }  
        return count;
    }
    
    public String getVersion() {
    	return version;
    }
	
	public LocalDate getDeprecationDate() {
		return deprecationDate;
	}
	
	public void setDeprecationDate(String date) {
		this.deprecationDate = LocalDate.parse( date );
	}
	
	public void setHypeLevel(int hype) {
		switch (hype) {
		case 1 :
			hypeLevel = "Meh";
		case 2 :
			hypeLevel = "What is dis";
		case 3 :
			hypeLevel = "Teach me Senpai!";
		case 4 :
			hypeLevel = "Hyped!";
		case 5 :
			hypeLevel = "Super Sayian Hype!";
		}
	}
	
	public String getHypeLevel() {
		return hypeLevel;
	}

	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + ", version=" + version + ", deprecationDate=" + deprecationDate.toString() + ", hypeLevel=" + hypeLevel + "]";
	}

}
