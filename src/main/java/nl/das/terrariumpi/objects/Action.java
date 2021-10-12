package nl.das.terrariumpi.objects;

import jakarta.json.bind.annotation.JsonbTransient;

public class Action {

	private String device;
	private Integer on_period;

	public Action() { }

	public Action(String device, int onPeriod) {
		this.device = device;
		this.on_period = onPeriod;
	}

	@JsonbTransient
	public int getOnPeriod() {
		return this.on_period;
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice (String device) {
		this.device = device;
	}

	public Integer getOn_period () {
		return this.on_period;
	}

	public void setOn_period (Integer on_period) {
		this.on_period = on_period;
	}
}