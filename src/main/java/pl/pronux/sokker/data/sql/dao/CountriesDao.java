package pl.pronux.sokker.data.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.pronux.sokker.data.sql.dto.CountryDto;
import pl.pronux.sokker.data.sql.dto.RegionDto;
import pl.pronux.sokker.model.Country;
import pl.pronux.sokker.model.Region;

public class CountriesDao {

	private Connection connection;

	public CountriesDao(Connection connection) {
		this.connection = connection;
	}

	public void addCountry(Country country) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("INSERT INTO countries(country_id,name,currency_name,currency_rate) VALUES (?,?,?,?)"); 
		pstm.setInt(1, country.getCountryId());
		pstm.setString(2, country.getName());
		pstm.setString(3, country.getCurrencyName());
		pstm.setDouble(4, country.getCurrencyRate());
		pstm.executeUpdate();
		pstm.close();

	}

	public List<Country> getCountries() throws SQLException {
		List<Country> countries = new ArrayList<Country>();
		PreparedStatement ps = connection.prepareStatement("SELECT country_id, name, currency_rate, currency_name FROM countries"); 
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Country country = new CountryDto(rs).getCountry();
			countries.add(country);
		}
		rs.close();
		ps.close();
		return countries;
	}

	public boolean existsCountry(int countryId) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT count(country_id) FROM countries WHERE country_id = ?"); 
		ps.setInt(1, countryId);
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

	public void updateCountry(Country country) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE countries SET currency_rate= ?, name = ? WHERE country_id = ?"); 

		ps.setDouble(1, country.getCurrencyRate());
		ps.setString(2, country.getName());
		ps.setInt(3, country.getCountryId());
		ps.executeUpdate();
		ps.close();
	}

	public boolean existsRegion(int regionId) throws SQLException {

		PreparedStatement ps = connection.prepareStatement("SELECT count(region_id) FROM regions WHERE region_id = ?"); 
		ps.setInt(1, regionId);
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

	public void updateRegion(Region region) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE regions SET weather = ? WHERE region_id = ?"); 

		ps.setInt(1, region.getWeather());
		ps.setInt(2, region.getRegionId());
		ps.executeUpdate();
		ps.close();
	}

	public void addRegion(Region region) throws SQLException {
		PreparedStatement pstm = connection.prepareStatement("INSERT INTO regions(region_id,country_id,name,weather) VALUES (?,?,?,?)"); 
		pstm.setInt(1, region.getRegionId());
		pstm.setInt(2, region.getCountryId());
		pstm.setString(3, region.getName());
		pstm.setInt(4, region.getWeather());
		pstm.executeUpdate();
		pstm.close();
	}

	public Region getRegion(int regionId) throws SQLException {
		Region region = null;
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM regions WHERE region_id = ?"); 
		ps.setInt(1, regionId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			region = new RegionDto(rs).getRegion();
		}
		rs.close();
		ps.close();
		return region;
	}
}
