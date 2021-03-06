package pl.pronux.sokker.model;

public class PlayerArchive  extends Person {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4061520477526034317L;
	
	public static final int EXISTS_IN_SOKKER_TRUE = 1;
	public static final int EXISTS_IN_SOKKER_FALSE = 2;
	public static final int EXISTS_IN_SOKKER_COMPLETED = 3;

	private int countryId;
	private int national;
	private int transferList;
	private int youthTeamId;
	private int age;
	private Money value;
	private Money wage;
	private int cards;
	private int goals;
	private int assists;
	private int matches;
	private int ntCards;
	private int ntMatches;
	private int ntAssists;
	private int ntGoals;
	private double injuryDays;
	private int skillForm;
	private int skillExperience;
	private int skillTeamwork;
	private int skillDiscipline;
	private int existsInSokker;
	private int height = 0;

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getNational() {
		return national;
	}

	public void setNational(int national) {
		this.national = national;
	}

	public int getTransferList() {
		return transferList;
	}

	public void setTransferList(int transferList) {
		this.transferList = transferList;
	}

	public int getYouthTeamId() {
		return youthTeamId;
	}

	public void setYouthTeamId(int youthTeamId) {
		this.youthTeamId = youthTeamId;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Money getValue() {
		return value;
	}

	public void setValue(Money value) {
		this.value = value;
	}

	public Money getWage() {
		return wage;
	}

	public void setWage(Money wage) {
		this.wage = wage;
	}

	public int getCards() {
		return cards;
	}

	public void setCards(int cards) {
		this.cards = cards;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public int getNtCards() {
		return ntCards;
	}

	public void setNtCards(int ntCards) {
		this.ntCards = ntCards;
	}

	public int getNtMatches() {
		return ntMatches;
	}

	public void setNtMatches(int ntMatches) {
		this.ntMatches = ntMatches;
	}

	public int getNtAssists() {
		return ntAssists;
	}

	public void setNtAssists(int ntAssists) {
		this.ntAssists = ntAssists;
	}

	public int getNtGoals() {
		return ntGoals;
	}

	public void setNtGoals(int ntGoals) {
		this.ntGoals = ntGoals;
	}

	public double getInjuryDays() {
		return injuryDays;
	}

	public void setInjuryDays(double injuryDays) {
		this.injuryDays = injuryDays;
	}

	public int getSkillForm() {
		return skillForm;
	}

	public void setSkillForm(int skillForm) {
		this.skillForm = skillForm;
	}

	public int getSkillExperience() {
		return skillExperience;
	}

	public void setSkillExperience(int skillExperience) {
		this.skillExperience = skillExperience;
	}

	public int getSkillTeamwork() {
		return skillTeamwork;
	}

	public void setSkillTeamwork(int skillTeamwork) {
		this.skillTeamwork = skillTeamwork;
	}

	public int getSkillDiscipline() {
		return skillDiscipline;
	}

	public void setSkillDiscipline(int skillDiscipline) {
		this.skillDiscipline = skillDiscipline;
	}

	public int getExistsInSokker() {
		return existsInSokker;
	}

	public void setExistsInSokker(int existsInSokker) {
		this.existsInSokker = existsInSokker;
	}

	public PlayerArchive(Player player) {
		super();
		this.setId(player.getId());
		this.setName(player.getName());
		this.setSurname(player.getSurname());
		this.setCountryId(player.getCountryfrom());
		this.setTeamId(player.getTeamId());
		this.national = player.getNational();
		this.transferList = player.getTransferList();
		this.youthTeamId = player.getYouthTeamId();
		this.age = player.getSkills()[0].getAge();
		this.value = player.getSkills()[0].getValue();
		this.wage = player.getSkills()[0].getSalary();
		this.cards = player.getSkills()[0].getCards();
		this.goals = player.getSkills()[0].getGoals();
		this.assists = player.getSkills()[0].getAssists();
		this.matches = player.getSkills()[0].getMatches();
		if(player.getNtSkills() != null && player.getNtSkills().length > 0) {
			this.ntCards = player.getNtSkills()[0].getNtCards();
			this.ntMatches = player.getNtSkills()[0].getNtMatches();
			this.ntAssists = player.getNtSkills()[0].getNtAssists();
			this.ntGoals = player.getNtSkills()[0].getNtGoals();
		}
		this.injuryDays = player.getSkills()[0].getInjurydays();
		this.skillForm = player.getSkills()[0].getForm();
		this.skillExperience = player.getSkills()[0].getExperience();
		this.skillTeamwork = player.getSkills()[0].getTeamwork();
		this.skillDiscipline = player.getSkills()[0].getDiscipline();
		this.setNote(player.getNote());
		this.existsInSokker = EXISTS_IN_SOKKER_TRUE;
		this.height = player.getHeight();
	}

	public PlayerArchive() {
	}
	
	public Player toPlayer() {
		Player player = new Player();
		player.setId(this.getId());
		player.setName(this.getName());
		player.setSurname(this.getSurname());
		player.setCountryfrom(this.getCountryId());
		player.setExistsInSokker(this.getExistsInSokker());
		player.setYouthTeamId(this.getYouthTeamId());
		player.setNote(this.getNote());
		return player;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}
	
}
