package pl.pronux.sokker.ui.widgets.tables;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pl.pronux.sokker.handlers.SettingsHandler;
import pl.pronux.sokker.model.League;
import pl.pronux.sokker.model.Player;
import pl.pronux.sokker.model.PlayerSkills;
import pl.pronux.sokker.model.PlayerStats;
import pl.pronux.sokker.model.SokkerDate;
import pl.pronux.sokker.model.Training;
import pl.pronux.sokker.resources.Messages;
import pl.pronux.sokker.ui.beans.Colors;
import pl.pronux.sokker.ui.beans.ConfigBean;
import pl.pronux.sokker.ui.handlers.DisplayHandler;
import pl.pronux.sokker.ui.resources.ColorResources;
import pl.pronux.sokker.ui.resources.Fonts;

public class PlayerTable extends SVTable<Player> {

	public static final int VALUE = 1;
	
	public static final int SALARY = 2;
	
	public static final int AGE = 3;
	
	public static final int FORM = 4;
	
	public static final int STAMINA = 5;
	
	public static final int PACE = 6;
	
	public static final int TECHNIQUE = 7;
	
	public static final int PASSING = 8;
	
	public static final int KEEPER = 9;
	
	public static final int DEFENDER = 10;
	
	public static final int PLAYMAKER = 11;
	
	public static final int SCORER = 12;
	
	public PlayerTable(Composite parent, int style) {
		super(parent, style);
		this.setVisible(false);
		this.setLinesVisible(true);
		this.setHeaderVisible(true);
		this.setFont(ConfigBean.getFontTable());

		String[] titles = new String[] {
				Messages.getString("table.date"), 
				Messages.getString("table.value"), 
				Messages.getString("table.salary"), 
				Messages.getString("table.age"), 
				Messages.getString("table.form"), 
				Messages.getString("table.stamina"), 
				Messages.getString("table.pace"), 
				Messages.getString("table.technique"), 
				Messages.getString("table.passing"), 
				Messages.getString("table.keeper"), 
				Messages.getString("table.defender"), 
				Messages.getString("table.playmaker"), 
				Messages.getString("table.scorer"), 
				Messages.getString("table.discipline"), 
				Messages.getString("table.experience"), 
				Messages.getString("table.teamwork"), 
				Messages.getString("table.formation"), 
				Messages.getString("table.training.type"), 
				Messages.getString("table.1st"), 
				Messages.getString("table.2nd"), 
				"" 
		};
		for (int j = 0; j < titles.length; j++) {
			TableColumn column = new TableColumn(this, SWT.RIGHT);
			column.setText(titles[j]);
			column.setResizable(false);
			column.setMoveable(false);
			if (titles[j].isEmpty()) {
				// column.setWidth(70);
				if (SettingsHandler.IS_LINUX) {
					column.pack();
				}
			} else {
				// column.setWidth(40);
				column.pack();
			}
		}
	}

	public void fill(Player player) {
		int max = 0;
		
		max = player.getSkills().length;
		for (int i = max - 1; i >= 0; i--) {
			int c = 0;
			TableItem item = new TableItem(this, SWT.NONE);
			if(!player.getSkills()[i].isPassTraining()) {
				item.setForeground(ColorResources.getDarkGray());
			}
			item.setData("date", player.getSkills()[i].getDate()); 
			item.setData("player_skill", player.getSkills()[i]); 
			item.setText(c++, player.getSkills()[i].getDate().getTrainingDate(SokkerDate.THURSDAY).toDateString());
			item.setText(c++, player.getSkills()[i].getValue().formatIntegerCurrency());
			item.setText(c++, player.getSkills()[i].getSalary().formatIntegerCurrency());
			item.setText(c++, String.valueOf(player.getSkills()[i].getAge()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getForm()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getStamina()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getPace()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getTechnique()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getPassing()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getKeeper()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getDefender()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getPlaymaker()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getScorer()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getDiscipline()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getExperience()));
			item.setText(c++, String.valueOf(player.getSkills()[i].getTeamwork()));
			if (player.getSkills()[i].getTraining() != null) {
				if (player.getSkills()[i].getTraining().getType() == Training.TYPE_PACE || player.getSkills()[i].getTraining().getType() == Training.TYPE_STAMINA) {
					item.setText(c++, Messages.getString("formation." + Training.FORMATION_ALL)); 
				} else {
					item.setText(c++, Messages.getString("formation." + player.getSkills()[i].getTraining().getFormation())); 
				}
				item.setText(c++, Messages.getString("training.type." + player.getSkills()[i].getTraining().getType() + ".short"));  
			} else {
				item.setText(c++, ""); 
				item.setText(c++, ""); 
			}

			if (player.getPlayerMatchStatistics() != null) {
				int week = player.getSkills()[i].getDate().getTrainingDate(SokkerDate.THURSDAY).getSokkerDate().getWeek();
				for (PlayerStats playerStats : player.getPlayerMatchStatistics()) {
					if (playerStats.getMatch().getWeek() == week) {
						if (playerStats.getFormation() >= 0 && playerStats.getFormation() <= 4 && playerStats.getTimePlayed() > 0) {
							League league = playerStats.getMatch().getLeague();

							if ((league.getType() == League.TYPE_LEAGUE || league.getType() == League.TYPE_PLAYOFF) && league.getIsOfficial() == League.OFFICIAL) {
								item.setFont(18, Fonts.getBoldFont(DisplayHandler.getDisplay(), item.getFont(18).getFontData()));
								item.setText(18, String.format("%s (%d')", Messages.getString("formation." + playerStats.getFormation()) , playerStats.getTimePlayed()));  
//								if(league.getType() == League.TYPE_LEAGUE) {
//									item.setImage(18, ImageResources.getImageResources("league.png"));
//								} else if(league.getType() == League.TYPE_PLAYOFF) {
//									item.setImage(18, ImageResources.getImageResources("playoff.png"));
//								}
								if (playerStats.getFormation() == PlayerStats.GK) {
									item.setBackground(18, Colors.getPositionGK());
								} else if (playerStats.getFormation() == PlayerStats.DEF) {
									item.setBackground(18, Colors.getPositionDEF());
								} else if (playerStats.getFormation() == PlayerStats.MID) {
									item.setBackground(18, Colors.getPositionMID());
								} else if (playerStats.getFormation() == PlayerStats.ATT) {
									item.setBackground(18, Colors.getPositionATT());
								}

							} else {
								c++;
								if (league.getIsOfficial() == League.OFFICIAL) {
									item.setFont(19, Fonts.getBoldFont(DisplayHandler.getDisplay(), item.getFont(19).getFontData()));
//									item.setImage(19, ImageResources.getImageResources("cup.png"));
								}
								if (playerStats.getFormation() == PlayerStats.GK) {
								item.setBackground(19, Colors.getPositionGK());
							} else if (playerStats.getFormation() == PlayerStats.DEF) {
								item.setBackground(19, Colors.getPositionDEF());
							} else if (playerStats.getFormation() == PlayerStats.MID) {
								item.setBackground(19, Colors.getPositionMID());
							} else if (playerStats.getFormation() == PlayerStats.ATT) {
								item.setBackground(19, Colors.getPositionATT());
							}
								item.setText(19, String.format("%s (%d')", Messages.getString("formation." + playerStats.getFormation()) , playerStats.getTimePlayed()));  
//								if(league.getType() == League.TYPE_LEAGUE) {
//									item.setImage(19, ImageResources.getImageResources("friendly_league.png"));
//								} else if(league.getType() == League.TYPE_FRIENDLY_MATCH) {
//									item.setImage(19, ImageResources.getImageResources("friendly_match.png"));
//								}
							}
						}
					}
				}
			} else {
				item.setText(18, ""); 
				item.setText(19, ""); 
			}
			if (i > 0) {
				PlayerSkills now = player.getSkills()[i];
				PlayerSkills before = player.getSkills()[i - 1];
				compare(now.getValue().toInt(), before.getValue().toInt(), item, 1);
				compare(now.getSalary().toInt(), before.getSalary().toInt(), item, 2);
				compare(now.getAge(), before.getAge(), item, 3);
				compare(now.getForm(), before.getForm(), item, 4);
				compare(now.getStamina(), before.getStamina(), item, 5);
				compare(now.getPace(), before.getPace(), item, 6);
				compare(now.getTechnique(), before.getTechnique(), item, 7);
				compare(now.getPassing(), before.getPassing(), item, 8);
				compare(now.getKeeper(), before.getKeeper(), item, 9);
				compare(now.getDefender(), before.getDefender(), item, 10);
				compare(now.getPlaymaker(), before.getPlaymaker(), item, 11);
				compare(now.getScorer(), before.getScorer(), item, 12);
				compare(now.getDiscipline(), before.getDiscipline(), item, 13);
				compare(now.getExperience(), before.getExperience(), item, 14);
				compare(now.getTeamwork(), before.getTeamwork(), item, 15);
			}
		}

		for (int i = 0; i < this.getColumnCount() - 1; i++) {
			this.getColumn(i).pack();
//			this.getColumn(i).setWidth(this.getColumn(i).getWidth() + 5);
		}

		if (this.getColumn(18).getWidth() < this.getColumn(19).getWidth()) {
			this.getColumn(18).setWidth(this.getColumn(19).getWidth());
		} else {
			this.getColumn(19).setWidth(this.getColumn(18).getWidth());
		}
	}

	private void compare(int now, int before, TableItem item, int index) {
		if (before < now) {
			item.setBackground(index, ConfigBean.getColorIncrease());
		} else if (before > now) {
			item.setBackground(index, ConfigBean.getColorDecrease());
		}
	}

}
