package nl.das.terraria.objects;

import java.time.LocalDateTime;

import jakarta.json.bind.annotation.JsonbTransient;
import nl.das.terraria.Util;

public class Ruleset {

	private Integer terrarium;
	private String active;
	private String from;
	private String to;
	private Integer temp_ideal;
	private Rule[] rules;

	public Ruleset() { }

	public Ruleset(int terrariumNr, String active, String from, String to, int idealTemp, Rule[] rules) {
		this.terrarium = terrariumNr;
		this.active = active;
		this.from = from;
		this.to = to;
		this.temp_ideal = idealTemp;
		this.rules = rules;
	}

	@JsonbTransient
	public void makeActive() {
		this.active = "yes";
	}

	@JsonbTransient
	public void makeInactive() {
		this.active = "no";
	}

	@JsonbTransient
	public boolean active(LocalDateTime now) {
		long nowMinutes = (now.getHour() * 60L) + now.getMinute();
		return this.active.equalsIgnoreCase("yes")
				&& ((nowMinutes >= Util.cvtStringToMinutes(this.from)) && (nowMinutes <= Util.cvtStringToMinutes(this.to)));
	}

	public String getFrom() {
		return this.from;
	}

	public String getTo() {
		return this.to;
	}

	@JsonbTransient
	public int getIdealTemp() {
		return this.temp_ideal;
	}

	@JsonbTransient
	public void addRule(int rulenr, Rule rule) {
		this.rules[rulenr] = rule;
	}

	public Rule[] getRules() {
		return this.rules;
	}

	public Integer getTerrarium () {
		return this.terrarium;
	}

	public void setTerrarium (Integer terrarium) {
		this.terrarium = terrarium;
	}

	public String getActive () {
		return this.active;
	}

	public void setActive (String active) {
		this.active = active;
	}

	public Integer getTemp_ideal () {
		return this.temp_ideal;
	}

	public void setTemp_ideal (Integer temp_ideal) {
		this.temp_ideal = temp_ideal;
	}

	public void setFrom (String from) {
		this.from = from;
	}

	public void setTo (String to) {
		this.to = to;
	}

	public void setRules (Rule[] rules) {
		this.rules = rules;
	}
}