package pl.pronux.sokker.ui.widgets.shells;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pl.pronux.sokker.actions.TeamManager;
import pl.pronux.sokker.handlers.SettingsHandler;
import pl.pronux.sokker.model.Coach;
import pl.pronux.sokker.model.Date;
import pl.pronux.sokker.model.SokkerDate;
import pl.pronux.sokker.model.Training;
import pl.pronux.sokker.resources.Messages;
import pl.pronux.sokker.ui.beans.ConfigBean;
import pl.pronux.sokker.ui.handlers.DisplayHandler;
import pl.pronux.sokker.ui.handlers.ViewerHandler;
import pl.pronux.sokker.ui.interfaces.IEvents;
import pl.pronux.sokker.ui.resources.Fonts;

public class TrainingEditShell extends Shell {
	
	private TeamManager teamManager = TeamManager.getInstance();
	
	private Combo typeCombo;

	private Combo formationCombo;

	private Display display;

	private Training training;

	private List<Coach> coaches;

	private Date date;

	private Monitor monitor;

	private Table trainingTable;

	private Table coachesTable;

	private Training tempTraining;

	public TrainingEditShell(Shell parent, int style, List<Coach> coaches, Training training) {
		super(parent, style);

		this.display = parent.getDisplay();
		monitor = display.getPrimaryMonitor();
		this.date = training.getDate().getTrainingDate(SokkerDate.THURSDAY);

		this.training = training;

		this.tempTraining = training.clone();

		this.coaches = coaches;

		coaches.remove(training.getHeadCoach());
		coaches.remove(training.getJuniorCoach());
		coaches.removeAll(training.getAssistants());

		this.setSize(650, 550);
		this.setMinimumSize(new org.eclipse.swt.graphics.Point(650, 550));
		this.setLocation(monitor.getClientArea().width / 2 - this.getSize().x / 2, monitor.getClientArea().height / 2 - this.getSize().y / 2);
		this.setFont(ConfigBean.getFontMain());

		this.setText(Messages.getString("confShell.title")); 

		this.setLayout(new FormLayout());

		addBody();

		fillTableTraining(trainingTable, this.tempTraining);
		fillTableCoaches(coachesTable, this.coaches);

		this.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});

	}

	public void setCoaches(List<Coach> coaches) {
		this.coaches = coaches;
	}

	private void addBody() {
		int height = 15;

		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(0, 10);
		formData.height = 30;

		Label labelTitle = new Label(this, SWT.NONE);
		labelTitle.setLayoutData(formData);
		labelTitle.setText(Messages.getString("training.title")); 
		labelTitle.setAlignment(SWT.CENTER);

		Font font = Fonts.getFont(DisplayHandler.getDisplay(), "Arial", 20, SWT.BOLD); 

		labelTitle.setFont(font);

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(labelTitle, 5);
		formData.height = 30;

		Label labelDate = new Label(this, SWT.NONE);
		labelDate.setLayoutData(formData);
		labelDate.setText(String.format("%s: %s (%s)", Messages.getString("training.date"), date.toDateString(), date.toTimeString()));  
		labelDate.setAlignment(SWT.CENTER);

		Font fontDate = Fonts.getFont(DisplayHandler.getDisplay(), "Arial", 16, SWT.BOLD); 

		labelDate.setFont(fontDate);

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(labelDate, 5);
		formData.height = 30;

		Label labelSeason = new Label(this, SWT.NONE);
		labelSeason.setLayoutData(formData);

		labelSeason.setText(Messages.getString("training.week") + " " + date.getSokkerDate().getWeek());  
		labelSeason.setAlignment(SWT.CENTER);
		labelSeason.setFont(fontDate);

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.top = new FormAttachment(labelSeason, 5);
		formData.width = 100;

		Label label1 = new Label(this, SWT.NONE);
		label1.setLayoutData(formData);
		label1.setText(Messages.getString("training.type")); 
		label1.pack();

		formData = new FormData();
		formData.left = new FormAttachment(label1, 15);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(labelSeason, 5);
		formData.height = height;
		typeCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(formData);

		typeCombo.add(Messages.getString("training.type." + Training.TYPE_STAMINA)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_KEEPER)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_PLAYMAKING)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_PASSING)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_TECHNIQUE)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_DEFENDING)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_STRIKER)); 
		typeCombo.add(Messages.getString("training.type." + Training.TYPE_PACE)); 
		typeCombo.setText(Messages.getString("training.type." + tempTraining.getType())); 

		typeCombo.setVisibleItemCount(10);

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.top = new FormAttachment(typeCombo, 5);
		formData.width = 100;

		Label label2 = new Label(this, SWT.NONE);
		label2.setLayoutData(formData);
		label2.setText(Messages.getString("formation")); 
		label2.pack();

		formData = new FormData();
		formData.left = new FormAttachment(label2, 15);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(typeCombo, 5);
		formData.height = height;
		formationCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		formationCombo.setLayoutData(formData);
		formationCombo.add(Messages.getString("formation." + Training.FORMATION_GK)); 
		formationCombo.add(Messages.getString("formation." + Training.FORMATION_DEF)); 
		formationCombo.add(Messages.getString("formation." + Training.FORMATION_MID)); 
		formationCombo.add(Messages.getString("formation." + Training.FORMATION_ATT)); 
		formationCombo.setText(Messages.getString("formation." + tempTraining.getFormation())); 

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(50, -10);
		formData.top = new FormAttachment(formationCombo, 15);
		formData.width = 100;

		Label label3 = new Label(this, SWT.NONE);
		label3.setLayoutData(formData);
		label3.setText(Messages.getString("coach.training")); 
		label3.pack();

		formData = new FormData();
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(50, -50);
		formData.top = new FormAttachment(label3, 5);
		formData.height = height * 15;

		trainingTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		trainingTable.setLinesVisible(false);
		trainingTable.setHeaderVisible(false);
		trainingTable.setLayoutData(formData);

		String[] columns = { "name_surname", "job", "" };   

		for (int j = 0; j < columns.length; j++) {
			TableColumn column = new TableColumn(trainingTable, SWT.LEFT);
			column.setText(columns[j]);
			column.setResizable(false);
			column.setMoveable(false);
			if (columns[j].isEmpty()) { 
				// column.setWidth(70);
				if (SettingsHandler.IS_LINUX) {
					column.pack();
				}
			} else {
				column.pack();
			}
		}

		formData = new FormData();
		formData.left = new FormAttachment(50, 50);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(formationCombo, 15);
		formData.width = 100;

		Label label4 = new Label(this, SWT.NONE);
		label4.setLayoutData(formData);
		label4.setText(Messages.getString("coach.available")); 
		label4.pack();

		formData = new FormData();
		formData.left = new FormAttachment(50, 50);
		formData.right = new FormAttachment(100, -10);
		formData.top = new FormAttachment(label3, 5);
		formData.height = height * 15;

		coachesTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		coachesTable.setLinesVisible(false);
		coachesTable.setHeaderVisible(false);
		coachesTable.setLayoutData(formData);

		String[] coachesColumns = { "name_surname", "" };  

		for (int j = 0; j < coachesColumns.length; j++) {
			TableColumn column = new TableColumn(coachesTable, SWT.LEFT);
			column.setText(coachesColumns[j]);
			column.setResizable(false);
			column.setMoveable(false);
			if (columns[j].isEmpty()) {
				// column.setWidth(70);
				if (SettingsHandler.IS_LINUX) {
					column.pack();
				}
			} else {
				column.pack();
			}
		}

		formData = new FormData();
		formData.right = new FormAttachment(coachesTable, -4);
		formData.left = new FormAttachment(trainingTable, 4);
		formData.top = new FormAttachment(trainingTable, 0, SWT.CENTER);

		final CCombo coachesCombo = new CCombo(this, SWT.FLAT | SWT.READ_ONLY);
		coachesCombo.setLayoutData(formData);

		coachesCombo.add(Messages.getString("coach.job." + Coach.JOB_HEAD)); 
		coachesCombo.add(Messages.getString("coach.job." + Coach.JOB_ASSISTANT)); 
		coachesCombo.add(Messages.getString("coach.job." + Coach.JOB_JUNIORS)); 
		coachesCombo.setText(Messages.getString("coach.job." + Coach.JOB_HEAD)); 

		formData = new FormData();
		formData.right = new FormAttachment(coachesTable, -4);
		formData.left = new FormAttachment(trainingTable, 4);
		formData.bottom = new FormAttachment(coachesCombo, -5);
		Button moveLeftButton = new Button(this, SWT.FLAT);
		moveLeftButton.setLayoutData(formData);
		moveLeftButton.setText("<"); 
		moveLeftButton.pack();

		moveLeftButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				TableItem[] items = coachesTable.getSelection();
				if (items.length != 1) {
					return;
				}
				Coach coach = (Coach) items[0].getData(Coach.class.getName());

				if (coachesCombo.getSelectionIndex() + 1 == Coach.JOB_HEAD) {
					if (tempTraining.getHeadCoach() != null) {
						coaches.add(tempTraining.getHeadCoach());
					}
					tempTraining.setHeadCoach(coach);
					coach.setJob(Coach.JOB_HEAD);
					coaches.remove(coach);
				}
				if (coachesCombo.getSelectionIndex() + 1 == Coach.JOB_JUNIORS) {
					if (tempTraining.getJuniorCoach() != null) {
						coaches.add(tempTraining.getJuniorCoach());
					}
					tempTraining.setJuniorCoach(coach);
					coach.setJob(Coach.JOB_JUNIORS);
					coaches.remove(coach);
				}
				if (coachesCombo.getSelectionIndex() + 1 == Coach.JOB_ASSISTANT) {
					tempTraining.getAssistants().add(coach);
					coach.setJob(Coach.JOB_ASSISTANT);
					coaches.remove(coach);
				}
				items[0].dispose();

				fillTableTraining(trainingTable, tempTraining);
				fillTableCoaches(coachesTable, coaches);
			}

		});

		formData = new FormData();
		formData.right = new FormAttachment(coachesTable, -4);
		formData.left = new FormAttachment(trainingTable, 4);
		formData.top = new FormAttachment(coachesCombo, 5);

		Button moveRightButton = new Button(this, SWT.FLAT);
		moveRightButton.setLayoutData(formData);
		moveRightButton.setText(">"); 
		moveRightButton.pack();

		moveRightButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				TableItem[] items = trainingTable.getSelection();
				if (items.length != 1) {
					return;
				}
				Coach coach = (Coach) items[0].getData(Coach.class.getName()); 

				if (coach.equals(tempTraining.getHeadCoach())) {
					tempTraining.setHeadCoach(null);
					coaches.add(coach);
				}
				if (coach.equals(tempTraining.getJuniorCoach())) {
					tempTraining.setJuniorCoach(null);
					coaches.add(coach);
				}
				if (tempTraining.getAssistants().contains(coach)) {
					tempTraining.getAssistants().remove(coach);
					coaches.add(coach);
				}
				items[0].dispose();

				fillTableCoaches(coachesTable, coaches);
			}

		});

		Button okButton = new Button(this, SWT.NONE);
		okButton.setText(Messages.getString("button.ok")); 

		formData = new FormData();
		formData.left = new FormAttachment(0, (this.getClientArea().width / 2) - 60 - 10);
		formData.top = new FormAttachment(coachesTable, 20);
		formData.height = 25;
		formData.width = 60;

		okButton.setLayoutData(formData);

		okButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {

				tempTraining.setFormation(formationCombo.getSelectionIndex());
				tempTraining.setType(typeCombo.getSelectionIndex() + 1);

				training.copy(tempTraining);
				if (tempTraining != null) {
					try {
						teamManager.updateTraining(tempTraining);
						ViewerHandler.getViewer().notifyListeners(IEvents.REFRESH_TRAININGS, new Event());
					} catch (SQLException e) {
						new BugReporter(TrainingEditShell.this.getDisplay()).openErrorMessage("Training edit shell -> update training", e);
					}
				}
				TrainingEditShell.this.close();
			}

		});

		formData = new FormData();
		formData.left = new FormAttachment(okButton, 20);
		formData.top = new FormAttachment(coachesTable, 20);
		formData.height = 25;
		formData.width = 60;

		Button closeButton = new Button(this, SWT.NONE);
		closeButton.setLayoutData(formData);
		closeButton.setText(Messages.getString("button.cancel")); 

		closeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TrainingEditShell.this.close();
			}
		});
		this.setDefaultButton(okButton);
	}

	private void fillTableCoaches(Table table, List<Coach> coaches) {
		if (coaches == null) {
			return;
		}

		// Turn off drawing to avoid flicker
		table.setRedraw(false);
		table.removeAll();
		TableItem item;
		int c = 0;
		for (Coach coach : coaches) {
			item = new TableItem(table, SWT.NONE);
			c = 0;
			item.setData(Coach.class.getName(), coach);
			item.setText(c++, coach.getSurname() + " " + coach.getName()); 
		}
		for (int i = 0; i < table.getColumnCount() - 1; i++) {
			table.getColumn(i).pack();
			table.getColumn(i).setWidth(table.getColumn(i).getWidth() + 5);
		}
		// Turn drawing back on
		table.setRedraw(true);
	}

	private void fillTableTraining(Table table, Training training) {
		if (training == null) {
			return;
		}
		// Turn off drawing to avoid flicker
		table.setRedraw(false);
		table.removeAll();
		TableItem item;
		int c = 0;
		Coach coach = training.getHeadCoach();
		if (coach != null) {
			item = new TableItem(table, SWT.NONE);
			c = 0;
			item.setData(Coach.class.getName(), coach); 
			item.setText(c++, coach.getSurname() + " " + coach.getName()); 
			item.setText(c++, Messages.getString("coach.job." + Coach.JOB_HEAD)); 
		}

		coach = training.getJuniorCoach();
		if (coach != null) {
			item = new TableItem(table, SWT.NONE);
			c = 0;
			item.setData(Coach.class.getName(), coach); 
			item.setText(c++, coach.getSurname() + " " + coach.getName()); 
			item.setText(c++, Messages.getString("coach.job." + Coach.JOB_JUNIORS)); 
		}

		// We remove all the table entries, sort our
		// rows, then add the entries

		for (Coach assistant : training.getAssistants()) {
			item = new TableItem(table, SWT.NONE);
			c = 0;
			item.setData(Coach.class.getName(), assistant); 
			item.setText(c++, assistant.getSurname() + " " + assistant.getName()); 
			item.setText(c++, Messages.getString("coach.job." + Coach.JOB_ASSISTANT)); 
		}
		for (int i = 0; i < table.getColumnCount() - 1; i++) {
			table.getColumn(i).pack();
			table.getColumn(i).setWidth(table.getColumn(i).getWidth() + 5);
		}
		// Turn drawing back on
		table.setRedraw(true);
	}

	@Override
	public void open() {
		super.open();
		while (!this.isDisposed()) {
			if (!this.getDisplay().readAndDispatch()) {
				this.getDisplay().sleep();
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// super.checkSubclass();
	}

}
