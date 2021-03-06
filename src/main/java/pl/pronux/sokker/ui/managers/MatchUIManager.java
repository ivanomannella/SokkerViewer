package pl.pronux.sokker.ui.managers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import pl.pronux.sokker.model.League;
import pl.pronux.sokker.ui.beans.Colors;
import pl.pronux.sokker.ui.resources.ImageResources;


public final class MatchUIManager {
	private static MatchUIManager instance = new MatchUIManager();
	
	private MatchUIManager() {
	}
	
	public static MatchUIManager instance() {
		return instance;
	}
	
	public Image getMatchImage(League league) {
		if (league != null) {
			if (league.getIsOfficial() == League.OFFICIAL) {
				if (league.getType() == League.TYPE_LEAGUE) {
					return ImageResources.getImageResources("league_match.png"); 
				} else if (league.getType() == League.TYPE_PLAYOFF && league.getIsCup() == League.CUP) {
					return ImageResources.getImageResources("playoff.png"); 
				} else if (league.getType() == League.TYPE_CUP && league.getIsCup() == League.CUP) {
					return ImageResources.getImageResources("cup.png"); 
				} else if (league.getType() == League.TYPE_JUNIOR_LEAGUE) {
					return ImageResources.getImageResources("juniors_league.png"); 
				}
			} else {
				if (league.getType() == League.TYPE_FRIENDLY_MATCH) {
					return ImageResources.getImageResources("friendly_match.png"); 
				} else if (league.getType() == League.TYPE_LEAGUE) {
					return ImageResources.getImageResources("friendly_league.png"); 
				}
			}
		}
		return null;
	}

	public Color getMatchColor(League league) {
		if (league != null) {
			if (league.getIsOfficial() == League.OFFICIAL) {
				if (league.getType() == League.TYPE_LEAGUE) {
					return Colors.getLeagueMatch();
				} else if (league.getType() == League.TYPE_PLAYOFF && league.getIsCup() == League.CUP) {
					return Colors.getPlayoff();
				} else if (league.getType() == League.TYPE_CUP && league.getIsCup() == League.CUP) {
					return Colors.getCup();
				} else if (league.getType() == League.TYPE_JUNIOR_LEAGUE) {
					return Colors.getJuniorsLeague();
				}
			} else {
				if (league.getType() == League.TYPE_FRIENDLY_MATCH) {
					return Colors.getFriendlyMatch();
				} else if (league.getType() == League.TYPE_LEAGUE) {
					return Colors.getFriendlyLeague();
				}
			}
		}
		return null;
	}
}
