package pl.pronux.sokker.downloader.managers;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.pronux.sokker.actions.PlayersManager;
import pl.pronux.sokker.data.sql.SQLSession;
import pl.pronux.sokker.data.sql.dao.PlayersDao;
import pl.pronux.sokker.downloader.xml.XMLDownloader;
import pl.pronux.sokker.downloader.xml.parsers.PlayerXmlParser;
import pl.pronux.sokker.downloader.xml.parsers.PlayersXmlParser;
import pl.pronux.sokker.model.Date;
import pl.pronux.sokker.model.Player;
import pl.pronux.sokker.model.Training;

public class PlayersXmlManager extends XmlManager<Player> {

	Map<String, String> teamsMap = new HashMap<String, String>();
	
	public String completeYouthTeamId() throws SQLException {
		String value = "-3"; //$NON-NLS-1$
		ArrayList<Integer> alPlayersID = new ArrayList<Integer>();

		try {

			PlayersDao playersDao = new PlayersDao(SQLSession.getConnection());

			alPlayersID = playersDao.getPlayersIDWithoutYouthTeamId();

			if (downloader.getStatus().equals("OK")) { //$NON-NLS-1$
				value = "0"; //$NON-NLS-1$
			} else {
				value = downloader.getErrorno();
			}
			// download xmls

			if (value.equals("0")) { //$NON-NLS-1$
				for (int i = 0; i < alPlayersID.size(); i++) {
					try {
						String xml = downloader.getPlayer(String.valueOf(alPlayersID.get(i)));
						PlayerXmlParser parser = new PlayerXmlParser();
						InputSource input = new InputSource(new StringReader(xml));
						try {
							parser.parseXmlSax(input, null);
						} catch (SAXException sex) {
							continue;
						}
						Player player = parser.getPlayer();
						if (player != null) {
							playersDao.updatePlayerYouthTeamID(player);
						} else {
							playersDao.updateNotExists(alPlayersID.get(i));
						}
					} catch (IOException ioex) {
					}
				}
			}
		} catch (final SQLException e) {
			throw e;
		}

		return value;

	}

	private List<Player> alPlayers;

	public PlayersXmlManager(String name, String destination, XMLDownloader downloader, Date currentDay) {
		super(name, destination, downloader, currentDay);
	}
	
	public PlayersXmlManager(String content, Date currentDay, int teamID) {
		super(content, currentDay, teamID);
	}

	public PlayersXmlManager(String destination, XMLDownloader downloader, Date currentDay) {
		super("players", destination, downloader, currentDay); //$NON-NLS-1$
	}

	@Override
	public void download() throws IOException {
		setContent(downloader.getPlayers(downloader.getTeamID()));
	}

	public void download(String teamID) throws IOException {
		teamsMap.put(teamID, downloader.getPlayers(teamID));
	}
	
	public void importToSQL(Training training) throws SQLException {
		new PlayersManager().addPlayers(alPlayers, training);
	}

	public List<Player> parseXML() throws SAXException {
		this.alPlayers = parseXML(getContent());
		return this.alPlayers;
	}

	public List<Player> parseXML(int teamID) throws SAXException {
		return parseXML(teamsMap.get(String.valueOf(teamID)));
	}
	
	@Override
	public void importToSQL() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Player> parseXML(String xml) throws SAXException {
		PlayersXmlParser playersXmlParser = new PlayersXmlParser();
		InputSource input = new InputSource(new StringReader(xml));
		try {
			playersXmlParser.parseXmlSax(input, null);
		} catch (SAXException ex) {
			input = new InputSource(new StringReader(filterCharacters(xml)));
			playersXmlParser.parseXmlSax(input, null);
		}
		return playersXmlParser.getAlPlayers();
	}

	@Override
	public boolean write() throws IOException {
		return write(getContent(), downloader.getTeamID());
	}

}
