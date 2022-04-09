package mbLib;

public class FdIntrestBean {

	
	String MaturityPeriod1,MaturityPeriod2,Regular, SeniorCitizen, Dates,Scheme;

	public String getDates() {
		return Dates;
	}
	public void setDates(String dates) {
		Dates = dates;
	}

	public String getScheme() {
		return Dates;
	}
	public void setScheme(String scheme) {
		Scheme = scheme;
	}

	public String getMaturityPeriod() {
		return MaturityPeriod1;
	}

	public String getMaturityPeriod1() {
		return MaturityPeriod1;
	}

	public void setMaturityPeriod1(String maturityPeriod1) {
		MaturityPeriod1 = maturityPeriod1;
	}

	public String getMaturityPeriod2() {
		return MaturityPeriod2;
	}

	public void setMaturityPeriod2(String maturityPeriod2) {
		MaturityPeriod2 = maturityPeriod2;
	}

	public void setMaturityPeriod(String maturityPeriod) {
		MaturityPeriod1 = maturityPeriod;
	}

	public String getRegular() {
		return Regular;
	}

	public void setRegular(String regular) {
		Regular = regular;
	}

	public String getSeniorCitizen() {
		return SeniorCitizen;
	}

	public void setSeniorCitizen(String seniorCitizen) {
		SeniorCitizen = seniorCitizen;
	}
}
