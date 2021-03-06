package pl.pronux.sokker.data.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.pronux.sokker.data.sql.dto.ClubArenaNameDto;
import pl.pronux.sokker.data.sql.dto.ClubBudgetDto;
import pl.pronux.sokker.data.sql.dto.ClubDto;
import pl.pronux.sokker.data.sql.dto.ClubFanclubDto;
import pl.pronux.sokker.data.sql.dto.ClubNameDto;
import pl.pronux.sokker.data.sql.dto.RankDto;
import pl.pronux.sokker.data.sql.dto.StandDto;
import pl.pronux.sokker.data.sql.dto.TrainingDto;
import pl.pronux.sokker.model.Club;
import pl.pronux.sokker.model.ClubArenaName;
import pl.pronux.sokker.model.ClubBudget;
import pl.pronux.sokker.model.ClubName;
import pl.pronux.sokker.model.ClubSupporters;
import pl.pronux.sokker.model.Date;
import pl.pronux.sokker.model.Rank;
import pl.pronux.sokker.model.SokkerDate;
import pl.pronux.sokker.model.Stand;
import pl.pronux.sokker.model.Training;

public class TeamsDao {
	private Connection connection;

	public TeamsDao(Connection connection) {
		this.connection = connection;
	}

	public List<Integer> getNotImportedClubsId() throws SQLException {
		Set<Integer> clubsId = new HashSet<Integer>();
		PreparedStatement ps = connection
				.prepareStatement("SELECT DISTINCT home_team_id FROM matches_team WHERE home_team_id NOT IN (select id from club) UNION SELECT DISTINCT away_team_id FROM matches_team WHERE away_team_id NOT IN (select id from club)"); 
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			clubsId.add(rs.getInt(1));
		}
		rs.close();
		ps.close();
		return new ArrayList<Integer>(clubsId);
	}

	public boolean existsStand(int location, int teamId) throws SQLException {

		PreparedStatement ps = connection.prepareStatement("SELECT count(id) FROM arena WHERE location = ? AND team_id = ?"); 
		ps.setInt(1, location);
		ps.setInt(2, teamId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getInt(1) > 0) {
				rs.close();
				ps.close();
				return true;
			} else {
				rs.close();
				ps.close();
				return false;
			}
		}
		return false;
	}

	public void addStand(Stand stand, Date date, int teamId) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("INSERT INTO arena(millis, location, capacity, type, days, roof, day, week, team_id) VALUES (?, ?, ?,?,?,?,?,?,?)"); 

		pstm.setLong(1, date.getMillis());
		pstm.setInt(2, stand.getLocation());
		pstm.setInt(3, stand.getCapacity());
		pstm.setInt(4, stand.getType());
		pstm.setDouble(5, stand.getConstructionDays());
		pstm.setInt(6, stand.getIsRoof());
		pstm.setInt(7, date.getSokkerDate().getDay());
		pstm.setInt(8, date.getSokkerDate().getWeek());
		pstm.setInt(9, teamId);

		pstm.executeUpdate();
		pstm.close();
	}

	public boolean getStandChanges(Stand stand, int teamId) throws SQLException {
		PreparedStatement ps = connection
				.prepareStatement("SELECT count(location) FROM arena WHERE location = ? AND id = (select MAX(id) FROM arena WHERE location = ? AND team_id = ? AND roof = ? AND days = ? and type = ? and capacity = ?)"); 

		ps.setInt(1, stand.getLocation());
		ps.setInt(2, stand.getLocation());
		ps.setInt(3, teamId);
		ps.setInt(4, stand.getIsRoof());
		ps.setDouble(5, stand.getConstructionDays());
		ps.setInt(6, stand.getType());
		ps.setInt(7, stand.getCapacity());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getInt(1) > 0) {
				rs.close();
				ps.close();
				return false;
			} else {
				rs.close();
				ps.close();
				return true;
			}
		}
		return false;
	}

	public void updateStand(Stand stand, Date date, int teamID) throws SQLException {
		PreparedStatement ps = connection
				.prepareStatement("UPDATE arena a SET capacity=?, type=?, roof=?, days=?, millis=?, day=?, week=? WHERE location=? AND week = (select max(week) from arena where location = ? and team_id = arena.team_id) AND day = (select max(day) from arena where week = arena.week and team_id = arena.team_id) AND team_id = ?"); 

		ps.setInt(1, stand.getCapacity());
		ps.setInt(2, stand.getType());
		ps.setInt(3, stand.getIsRoof());
		ps.setDouble(4, stand.getConstructionDays());
		ps.setLong(5, date.getMillis());
		ps.setInt(6, date.getSokkerDate().getDay());
		ps.setInt(7, date.getSokkerDate().getWeek());
		ps.setInt(8, stand.getLocation());
		ps.setInt(9, stand.getLocation());
		ps.setInt(10, teamID);

		ps.executeUpdate();
		ps.close();

	}

	public Club getClub(int teamID) throws SQLException {
		Club club = new Club();
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM club WHERE id = ?"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			club = new ClubDto(rs).getClub();
		}
		rs.close();
		ps.close();
		return club;
	}

	public Set<Integer> getVisitedCountries(int teamId) throws SQLException {
		Set<Integer> visistedCountries = new HashSet<Integer>();
		PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT c.country FROM club as c, matches_team as m where m.away_team_id = ? and c.id = m.home_team_id"); 
		ps.setInt(1, teamId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			visistedCountries.add(rs.getInt(1));
		}
		rs.close();
		ps.close();
		return visistedCountries;
	}

	public Set<Integer> getInvitedCountries(int teamID) throws SQLException {
		Set<Integer> invitedCountries = new HashSet<Integer>();
		PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT c.country FROM club as c, matches_team as m where m.home_team_id = ? and c.id = m.away_team_id"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			invitedCountries.add(rs.getInt(1));
		}
		rs.close();
		ps.close();
		return invitedCountries;
	}

	public void updateClubDataFanclub(int teamID, ClubSupporters clubData, Date date) throws SQLException {
		PreparedStatement pstm = connection
				.prepareStatement("UPDATE club_data_fanclub c SET " + "fanclubcount=?, " + "fanclubmood=?,"   
						+ "millis = ? WHERE id_club_fk = ? AND week = (select max(week) from club_data_fanclub where id_club_fk = c.id_club_fk) AND day = (select max(day) from club_data_fanclub where week = c.week and id_club_fk = c.id_club_fk)"); 
		pstm.setInt(1, clubData.getFanclubcount());
		pstm.setInt(2, clubData.getFanclubmood());
		pstm.setLong(3, date.getMillis());
		pstm.setInt(4, teamID);

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateClubDataFanclubForDay(int teamID, ClubSupporters clubData, ClubSupporters clubSupporters) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("UPDATE club_data_fanclub c SET " + "fanclubcount=?, " + "fanclubmood=?,"   
				+ "millis = ? WHERE id_club_fk = ? AND id_data = ?"); 
		pstm.setInt(1, clubData.getFanclubcount());
		pstm.setInt(2, clubData.getFanclubmood());
		pstm.setLong(3, clubSupporters.getDate().getMillis());
		pstm.setInt(4, teamID);
		pstm.setInt(5, clubSupporters.getId());

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateClubDataMoney(int teamID, ClubBudget clubData, Date date) throws SQLException {
		PreparedStatement pstm = connection
				.prepareStatement("UPDATE club_data_money c SET " + "money=?, "  
						+ "millis = ? WHERE id_club_fk = ? AND week = (select max(week) from club_data_money where id_club_fk = c.id_club_fk) AND day = (select max(day) from club_data_money where week = c.week and id_club_fk = c.id_club_fk)"); 

		pstm.setInt(1, clubData.getMoney().toInt());
		pstm.setLong(2, date.getMillis());
		pstm.setInt(3, teamID);

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateClubDataMoney(int teamID, ClubBudget clubData, ClubBudget clubBudget) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("UPDATE club_data_money c SET " + "money=?, "  
				+ "millis = ? WHERE id_club_fk = ? AND id_data = ?"); 

		pstm.setInt(1, clubData.getMoney().toInt());
		pstm.setLong(2, clubBudget.getDate().getMillis());
		pstm.setInt(3, teamID);
		pstm.setInt(4, clubBudget.getId());

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateClubDateCreated(Club club) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE club SET " + "date_created = ? WHERE id = ?");  

		ps.setLong(1, club.getDateCreated().getMillis());
		ps.setInt(2, club.getId());

		ps.executeUpdate();
		ps.close();
	}

	public void updateRank(Rank rank, Date date, int rankID) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE rank SET rank = ?, day = ?, week = ?, millis = ? WHERE id_data = ?");  

		ps.setDouble(1, rank.getRank());
		ps.setInt(2, date.getSokkerDate().getDay());
		ps.setInt(3, date.getSokkerDate().getWeek());
		ps.setLong(4, date.getMillis());
		ps.setInt(5, rankID);

		ps.executeUpdate();
		ps.close();
	}

	public void updateClubImagePath(Club club) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("UPDATE club SET image_path = ? WHERE id = ?"); 

		pstm.setString(1, club.getImagePath());
		pstm.setInt(2, club.getId());

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateClubName(Club club, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE club_name SET name= ?, day = ?, week =?, millis = ? WHERE id_club_fk = ?"); 

		ps.setString(1, club.getClubName().get(0).getName());
		ps.setInt(2, date.getSokkerDate().getDay());
		ps.setInt(3, date.getSokkerDate().getWeek());
		ps.setLong(4, date.getMillis());
		ps.setInt(5, club.getId());

		ps.executeUpdate();
		ps.close();
	}

	public void updateClubArenaName(Club club, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE club_arena_name SET arena_name= ?, day = ?, week =?, millis = ? WHERE id_club_fk = ?"); 

		ps.setString(1, club.getArena().getArenaNames().get(0).getArenaName());
		ps.setInt(2, date.getSokkerDate().getDay());
		ps.setInt(3, date.getSokkerDate().getWeek());
		ps.setLong(4, date.getMillis());
		ps.setInt(5, club.getId());
		ps.executeUpdate();
		ps.close();
	}

	public void addClubName(int teamId, ClubName clubName, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO club_name (id_club_fk,millis,name, day, week) VALUES (?, ?, ?, ?, ?)"); 
		ps.setInt(1, teamId);
		ps.setLong(2, date.getMillis());
		ps.setString(3, clubName.getName());
		ps.setInt(4, date.getSokkerDate().getDay());
		ps.setInt(5, date.getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void addClubDataMoney(int teamId, ClubBudget cData, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO club_data_money (id_club_fk,millis,money, day, week) VALUES (?, ?, ?, ?, ?)"); 
		ps.setInt(1, teamId);
		ps.setLong(2, date.getMillis());
		ps.setInt(3, cData.getMoney().toInt());
		ps.setInt(4, date.getSokkerDate().getDay());
		ps.setInt(5, date.getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void addClubDataFanclub(int teamID, ClubSupporters cData, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO club_data_fanclub (id_club_fk,millis,fanclubcount,fanclubmood,day,week) VALUES (?, ?, ?, ?, ?, ?)"); 
		ps.setInt(1, teamID);
		ps.setLong(2, date.getMillis());
		ps.setInt(3, cData.getFanclubcount());
		ps.setInt(4, cData.getFanclubmood());
		ps.setInt(5, date.getSokkerDate().getDay());
		ps.setInt(6, date.getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public boolean ifNotNullClubDateCreated() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT date_created FROM club"); 

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getObject(1) != null) {
				rs.close();
				ps.close();
				return true;
			} else {
				rs.close();
				ps.close();
				return false;
			}
		}
		return false;
	}

	public void addClubArenaName(int teamID, ClubArenaName clubName, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO club_arena_name (id_club_fk, millis, arena_name, day, week) VALUES (?, ?, ?, ?,?)"); 
		ps.setInt(1, teamID);
		ps.setLong(2, date.getMillis());
		ps.setString(3, clubName.getArenaName());
		ps.setInt(4, date.getSokkerDate().getDay());
		ps.setInt(5, date.getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void addClub(Club club) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("INSERT INTO club(id,country,region, image_path, date_created,juniors_max) VALUES (?,?,?,?,?,?)"); 

		pstm.setInt(1, club.getId());
		pstm.setInt(2, club.getCountry());
		pstm.setInt(3, club.getRegionId());
		pstm.setString(4, club.getImagePath());
		if (club.getDateCreated() != null) {
			pstm.setLong(5, club.getDateCreated().getMillis());
		} else {
			pstm.setNull(5, java.sql.Types.DATE);
		}

		pstm.setInt(6, club.getJuniorsMax());

		pstm.executeUpdate();
		pstm.close();
	}

	public boolean existsClub(int id) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT count(id) FROM club WHERE id = ?"); 
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getInt(1) == 1) {
				rs.close();
				ps.close();
				return true;
			} else {
				rs.close();
				ps.close();
				return false;
			}
		}
		return false;
	}

	public List<Stand> getStands(int teamID) throws SQLException {
		
		List<Stand> alStands = new ArrayList<Stand>();

		PreparedStatement ps = connection.prepareStatement("SELECT * FROM arena WHERE id IN (SELECT MAX(id) FROM arena WHERE team_id = ? GROUP BY location) AND team_id = ?"); 

		ps.setInt(1, teamID);
		ps.setInt(2, teamID);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Stand stand = new StandDto(rs).getStand();
			alStands.add(stand);
		}
		rs.close();
		ps.close();
		return alStands;
	}

	public SokkerDate getLastMoneySokkerDate() throws SQLException {
		SokkerDate sokkerDate = null;
		PreparedStatement ps = connection
				.prepareStatement("select day, week from club_data_money c where week = (select max(week) from club_data_money) and day = (select max(day) from club_data_money where week = c.week)"); 

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			sokkerDate = new SokkerDate();
			sokkerDate.setDay(rs.getInt(1));
			sokkerDate.setWeek(rs.getInt(2));
		}
		rs.close();
		ps.close();
		return sokkerDate;
	}

	public SokkerDate getLastFanclubSokkerDate() throws SQLException {
		SokkerDate sokkerDate = null;
		PreparedStatement ps = connection
				.prepareStatement("select day, week from club_data_fanclub c where week = (select max(week) from club_data_fanclub) and day = (select max(day) from club_data_fanclub where week = c.week)"); 

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			sokkerDate = new SokkerDate();
			sokkerDate.setDay(rs.getInt(1));
			sokkerDate.setWeek(rs.getInt(2));
		}

		rs.close();
		ps.close();

		return sokkerDate;
	}

	public Double getLastClubRank(int teamID) throws SQLException {
		Double cRank = 0.0;
		PreparedStatement ps = connection
				.prepareStatement("select rank from rank t where t.week = (select max(week) from rank where t.id_club_fk = id_club_fk) AND t.day = (select max(day) from rank where week = t.week and t.id_club_fk = id_club_fk) AND id_club_fk = ?"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cRank = rs.getDouble(1);
		}
		rs.close();
		ps.close();

		return cRank;
	}

	public Rank getRankForDay(int teamId, Date date) throws SQLException {
		PreparedStatement ps = null;
		Rank rank = null;
		int day = date.getSokkerDate().getDay();
		int week = date.getSokkerDate().getWeek();
		if (day >= SokkerDate.MONDAY && day < SokkerDate.THURSDAY) {
			ps = connection.prepareStatement("select * from rank where id_club_fk = ? and day >= 2 and day < 5 and week = ?"); 
			ps.setInt(1, teamId);
			ps.setInt(2, week);
		} else if (day < SokkerDate.MONDAY || day >= SokkerDate.THURSDAY) {
			ps = connection.prepareStatement("select * from rank where id_club_fk = ? and (day >= 5 and week = ?) or (day < 2 and week = ?)"); 
			ps.setInt(1, teamId);
			if (day < SokkerDate.MONDAY) {
				ps.setInt(2, week - 1);
				ps.setInt(3, week);
			} else {
				ps.setInt(2, week);
				ps.setInt(3, week + 1);
			}
		}
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			rank = new RankDto(rs).getRankDto();
		}
		rs.close();
		ps.close();

		return rank;
	}

	public ClubSupporters getSupportersForDay(int teamID, Date date) throws SQLException {
		PreparedStatement ps = null;
		ClubSupporters fanclub = null;
		int week = date.getSokkerDate().getWeek();
		int day = date.getSokkerDate().getDay();
		if (day < SokkerDate.MONDAY) {
			ps = connection.prepareStatement("select * from club_data_fanclub where id_club_fk = ? and ((day >= 2 and week = ?) or (day < 2 and week = ?))"); 
			ps.setInt(1, teamID);
			ps.setInt(2, week - 1);
			ps.setInt(3, week);
		} else {
			ps = connection.prepareStatement("select * from club_data_fanclub where id_club_fk = ? and ((day >= 2 and week = ?) or (day < 2 and week = ?))"); 
			ps.setInt(1, teamID);
			ps.setInt(2, week);
			ps.setInt(3, week + 1);
		}

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			fanclub = new ClubFanclubDto(rs).getClubFanclub();
		}

		rs.close();
		ps.close();

		return fanclub;
	}

	public ClubBudget getBudgetForDay(int teamID, Date date) throws SQLException {
		ClubBudget clubBudget = null;
		int week = date.getSokkerDate().getWeek();
		PreparedStatement ps = connection.prepareStatement("select * from club_data_money where id_club_fk = ? and week = ?"); 
		ps.setInt(1, teamID);
		ps.setInt(2, week);

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			clubBudget = new ClubBudgetDto(rs).getClubBudget();
		}

		rs.close();
		ps.close();
		return clubBudget;
	}

	public String getLastClubName(int teamID) throws SQLException {
		String cName = ""; 
		PreparedStatement ps = connection
				.prepareStatement("SELECT name FROM club_name c WHERE id_club_fk = ? AND week = (select max(week) from club_name where c.id_club_fk = id_club_fk) and day = (select max(day) from club_name where week = c.week and c.id_club_fk = id_club_fk)"); 
		ps.setInt(1, teamID);
		// ps.setInt(2, id_club);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cName = rs.getString(1);
		}
		rs.close();
		ps.close();

		return cName;
	}

	public String getLastClubArenaName(int teamID) throws SQLException {
		String cArenaName = ""; 
		PreparedStatement ps = connection
				.prepareStatement("SELECT arena_name FROM club_arena_name c WHERE id_club_fk = ? AND week = (select max(week) from club_arena_name where c.id_club_fk = id_club_fk) and day = (select max(day) from club_arena_name where week = c.week and c.id_club_fk = id_club_fk)"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cArenaName = rs.getString(1);
		}
		rs.close();
		ps.close();
		return cArenaName;
	}

	public void clearArena() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM arena"); 
		ps.executeUpdate();
		ps.close();
	}

	public void clearClub() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM club"); 
		ps.executeUpdate();
		ps.close();
	}

	public void deleteClub(int id) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM club WHERE id = ?"); 
		ps.setInt(1, id);
		ps.executeUpdate();
		ps.close();
	}

	public List<ClubName> getClubName(int teamID) throws SQLException {
		List<ClubName> alClubName = new ArrayList<ClubName>();
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM club_name WHERE id_club_fk = ?"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ClubName cName = new ClubNameDto(rs).getClubName();
			alClubName.add(cName);
		}
		rs.close();
		ps.close();
		return alClubName;
	}

	public List<ClubBudget> getClubDataMoney(int teamID) throws SQLException {
		List<ClubBudget> clubBudgets = new ArrayList<ClubBudget>();
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM club_data_money WHERE id_club_fk = ? ORDER BY week, day"); 
		ps.setInt(1, teamID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ClubBudget clubBudget = new ClubBudgetDto(rs).getClubBudget();
			clubBudgets.add(clubBudget);
		}
		rs.close();
		ps.close();

		return clubBudgets;
	}

	public List<ClubSupporters> getClubDataFanclub(int clubId) throws SQLException {
		List<ClubSupporters> alClubDataFanclub = new ArrayList<ClubSupporters>();

		PreparedStatement ps = connection.prepareStatement("SELECT * FROM club_data_fanclub WHERE id_club_fk = ? ORDER BY week, day"); 
		ps.setInt(1, clubId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ClubSupporters cData = new ClubFanclubDto(rs).getClubFanclub();
			alClubDataFanclub.add(cData);
		}
		rs.close();
		ps.close();

		return alClubDataFanclub;
	}

	public List<ClubArenaName> getClubArenaName(int clubId) throws SQLException {
		List<ClubArenaName> alClubArenaName = new ArrayList<ClubArenaName>();

		PreparedStatement ps = connection.prepareStatement("SELECT * FROM club_arena_name where id_club_fk = ? order by week,day"); 
		ps.setInt(1, clubId);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ClubArenaName cArenaName = new ClubArenaNameDto(rs).getClubArenaName();
			alClubArenaName.add(cArenaName);
		}
		rs.close();
		ps.close();

		return alClubArenaName;
	}

	public List<Club> getClubs() throws SQLException {
		List<Club> clubs = new ArrayList<Club>();
		
		PreparedStatement ps = connection.prepareStatement("SELECT id,country,region, date_created FROM club"); 
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Club club = new Club();
			club.setId(rs.getInt("id")); 
			club.setCountry(rs.getInt("country")); 
			club.setRegionId(rs.getInt("region")); 
			club.setDateCreated(new Date(rs.getLong("date_created"))); 
			clubs.add(club);
		}
		rs.close();
		ps.close();
		return clubs;
	}

	public List<Rank> getRank(int clubId) throws SQLException {
		List<Rank> alRank = new ArrayList<Rank>();
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM rank WHERE id_club_fk = ? ORDER BY week DESC,day DESC"); 
		ps.setInt(1, clubId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Rank rank = new RankDto(rs).getRankDto();
			alRank.add(rank);
		}
		rs.close();
		ps.close();

		return alRank;
	}

	public void addRank(int clubId, Rank rank, Date date) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO rank (id_club_fk, millis, rank, day, week) VALUES (?, ?, ?, ?, ?)"); 
		ps.setInt(1, clubId);
		ps.setLong(2, date.getMillis());
		ps.setDouble(3, rank.getRank());
		ps.setInt(4, date.getSokkerDate().getDay());
		ps.setInt(5, date.getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void deleteRank(int clubId, Rank rank) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM rank where id_club_fk = ? and millis = ? and rank = ? and week = ? and day = ?"); 
		ps.setInt(1, clubId);
		ps.setLong(2, rank.getDate().getMillis());
		ps.setDouble(3, rank.getRank());
		ps.setInt(4, rank.getDate().getSokkerDate().getWeek());
		ps.setInt(5, rank.getDate().getSokkerDate().getDay());
		ps.executeUpdate();
		ps.close();
	}

	public void deleteClubArenaName(int clubId, ClubArenaName clubName) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM club_arena_name where id_club_fk = ? and millis = ? and arena_name = ? and day = ? and week = ?"); 
		ps.setInt(1, clubId);
		ps.setLong(2, clubName.getDate().getMillis());
		ps.setString(3, clubName.getArenaName());
		ps.setInt(4, clubName.getDate().getSokkerDate().getDay());
		ps.setInt(5, clubName.getDate().getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void deleteClubName(int clubId, ClubName clubName) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("DELETE FROM club_name where id_club_fk = ? and millis = ? and name = ? and day = ? and week = ?"); 
		ps.setInt(1, clubId);
		ps.setLong(2, clubName.getDate().getMillis());
		ps.setString(3, clubName.getName());
		ps.setInt(4, clubName.getDate().getSokkerDate().getDay());
		ps.setInt(5, clubName.getDate().getSokkerDate().getWeek());
		ps.executeUpdate();
		ps.close();
	}

	public void updateJuniorsMax(int id, int juniorsMax) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE club SET " + "juniors_max = ? WHERE id = ?");  

		ps.setInt(1, juniorsMax);
		ps.setLong(2, id);

		ps.executeUpdate();
		ps.close();
	}

	public void addTraining(Training training) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("INSERT INTO training(millis, type, formation, note, day, week) VALUES (?,?,?,?,?,?)"); 
		pstm.setLong(1, training.getDate().getMillis());
		pstm.setInt(2, training.getType());
		pstm.setInt(3, training.getFormation());
		pstm.setString(4, training.getNote());
		pstm.setInt(5, training.getDate().getSokkerDate().getDay());
		pstm.setInt(6, training.getDate().getSokkerDate().getWeek());
		pstm.executeUpdate();
		pstm.close();

	}

	public List<Training> getTrainings() throws SQLException {
		List<Training> alTraining = new ArrayList<Training>();
		PreparedStatement pstm = connection.prepareStatement("SELECT id_training, millis, type, formation, note, day, week, reported FROM training ORDER BY week DESC,day DESC"); 
		ResultSet rs = pstm.executeQuery();

		while (rs.next()) {
			Training training = new TrainingDto(rs).getTraining();
			alTraining.add(training);
		}
		rs.close();
		pstm.close();

		return alTraining;
	}

	public int getTrainingId(Training training) throws SQLException {

		int id = -1;
		PreparedStatement pstm = connection.prepareStatement("SELECT id_training FROM training WHERE day = ? and week = ?"); 
		pstm.setInt(1, training.getDate().getSokkerDate().getDay());
		pstm.setInt(2, training.getDate().getSokkerDate().getWeek());
		ResultSet rs = pstm.executeQuery();

		while (rs.next()) {
			id = rs.getInt(1);
		}
		rs.close();
		pstm.close();

		return id;
	}

	public Training getLastTraining() throws SQLException {
		Training training = null;
		PreparedStatement ps = connection.prepareStatement("select * from training t where week = (select max(week) from training) and day = (select max(day) from training where week = t.week)"); 

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			training = new TrainingDto(rs).getTraining();
		}
		rs.close();
		ps.close();
		return training;
	}

	public Training getTrainingForDay(Date date) throws SQLException {
		// SokkerDate sokkerDate = null;
		Training training = null;
		int week = date.getSokkerDate().getWeek();
		int day = date.getSokkerDate().getDay();
		PreparedStatement ps = connection.prepareStatement("select * from training where (day >= 5 and week = ?) or (day < 5 and week = ?)"); 
		if (day >= SokkerDate.THURSDAY) {
			ps.setInt(1, week);
			ps.setInt(2, week + 1);

		} else {
			ps.setInt(1, week - 1);
			ps.setInt(2, week);
		}

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			// sokkerDate = new SokkerDate(rs.getInt("day"), rs.getInt("week"));
			training = new TrainingDto(rs).getTraining();
		}

		rs.close();
		ps.close();

		return training;
	}

	public void updateReportedTrainings() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE training SET reported = false"); 
		ps.executeUpdate();
		ps.close();
	}

	public void updateTraining(Training training) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE training SET type = ?, formation = ? WHERE id_training = ?"); 

		ps.setInt(1, training.getType());
		ps.setInt(2, training.getFormation());
		ps.setInt(3, training.getId());

		ps.executeUpdate();
		ps.close();
	}
}