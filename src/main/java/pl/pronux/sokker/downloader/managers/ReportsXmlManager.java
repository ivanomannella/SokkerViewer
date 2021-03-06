package pl.pronux.sokker.downloader.managers;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.pronux.sokker.actions.TeamManager;
import pl.pronux.sokker.downloader.xml.XMLDownloader;
import pl.pronux.sokker.downloader.xml.parsers.ReportsXmlParser;
import pl.pronux.sokker.model.Date;
import pl.pronux.sokker.model.Report;

public class ReportsXmlManager extends XmlManager<Report> {

	private TeamManager teamManager = TeamManager.getInstance();

	private List<Report> reports;

	public ReportsXmlManager(String name, String destination, XMLDownloader downloader, Date currentDay) {
		super(name, destination, downloader, currentDay);
	}

	public ReportsXmlManager(String destination, XMLDownloader downloader, Date currentDay) {
		super("reports", destination, downloader, currentDay); 
	}

	public ReportsXmlManager(String content, Date currentDay, int teamID) {
		super(content, currentDay, teamID);
	}

	@Override
	public void download() throws IOException {
		setContent(getDownloader().getReports());
	}

	@Override
	public void importToSQL() throws SQLException {
		teamManager.importReports(this.reports);
	}

	public List<Report> parseXML() throws SAXException {
		return parseXML(getContent());
	}

	@Override
	public List<Report> parseXML(String xml) throws SAXException {
		ReportsXmlParser reportsXmlParser = new ReportsXmlParser();
		InputSource input = new InputSource(new StringReader(xml));
		try {
			reportsXmlParser.parseXmlSax(input, null);
		} catch (SAXException ex) {
			input = new InputSource(new StringReader(filterCharacters(xml)));
			reportsXmlParser.parseXmlSax(input, null);
		}

		this.reports = reportsXmlParser.getReports();
		return reports;
	}

}
