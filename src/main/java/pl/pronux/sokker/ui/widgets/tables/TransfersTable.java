package pl.pronux.sokker.ui.widgets.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pl.pronux.sokker.comparators.TransferComparator;
import pl.pronux.sokker.handlers.SettingsHandler;
import pl.pronux.sokker.interfaces.SVComparator;
import pl.pronux.sokker.model.Transfer;
import pl.pronux.sokker.resources.Messages;
import pl.pronux.sokker.ui.beans.ConfigBean;
import pl.pronux.sokker.ui.listeners.SortTableListener;
import pl.pronux.sokker.ui.resources.Fonts;
import pl.pronux.sokker.ui.resources.ImageResources;
import pl.pronux.sokker.ui.widgets.interfaces.IViewSort;

public class TransfersTable extends SVTable<Transfer> implements IViewSort<Transfer> {

	private List<Transfer> transfers = new ArrayList<Transfer>();

	private int idClub;

	private TransferComparator comparator;

	public TransfersTable(Composite parent, int style) {
		super(parent, style);

		comparator = new TransferComparator();
		comparator.setColumn(TransferComparator.DATE);
		comparator.setDirection(TransferComparator.ASCENDING);

		this.setLinesVisible(true);
		this.setHeaderVisible(true);
		this.setFont(ConfigBean.getFontTable());

		String[] columns = {
				" ", 
				Messages.getString("table.date"), 
				Messages.getString("table.name"), 
				Messages.getString("table.surname"), 
				Messages.getString("table.from"), 
				Messages.getString("table.to"), 
				Messages.getString("table.price"), 
				Messages.getString("table.value"), 
				"" 
		};

		for (int j = 0; j < columns.length; j++) {
			TableColumn column = new TableColumn(this, SWT.NONE);

			if (j > 5 && j < columns.length - 1) {
				column.setAlignment(SWT.RIGHT);
			} else {
				column.setAlignment(SWT.LEFT);
			}

			column.setText(columns[j]);
			column.setResizable(false);
			column.setMoveable(false);

			if (j == columns.length - 1) {
				// column.setWidth(70);
				if (SettingsHandler.IS_LINUX) {
					column.pack();
				}
			} else {
				// column.setWidth(40);
				column.pack();
				column.addSelectionListener(new SortTableListener<Transfer>(this, comparator));
			}
		}
		
		this.setSortColumn(this.getColumn(comparator.getColumn()));
		this.setSortDirection(comparator.getDirection());
	}

	public void fill(List<Transfer> alTransfers, int idClub) {
		this.idClub = idClub;
		this.transfers = alTransfers;
		
		// Turn off drawing to avoid flicker
		this.setRedraw(false);

		// We remove all the table entries, sort our
		// rows, then add the entries
		this.remove(0, this.getItemCount() - 1);
		for (Transfer transfer : alTransfers) {
			TableItem item = new TableItem(this, SWT.NONE);
			int c = 0;
			item.setData(Transfer.class.getName(), transfer);
			if (idClub == transfer.getSellerTeamId()) {
				item.setImage(c++, ImageResources.getImageResources("outcoming.png")); 
			} else {
				item.setImage(c++, ImageResources.getImageResources("incoming.png")); 
			}
			item.setText(c++, transfer.getDate().toDateTimeString());
			if (transfer.getPlayer() != null) {
				item.setText(c++, transfer.getPlayer().getName());
				item.setText(c++, transfer.getPlayer().getSurname());
			} else {
				item.setText(c++, ""); 
				item.setText(c++, ""); 
			}

			item.setText(c++, transfer.getSellerTeamName());
			item.setText(c++, transfer.getBuyerTeamName());

			item.setFont(c, Fonts.getBoldFont(item.getDisplay(), item.getFont().getFontData()));
			item.setText(c++, transfer.getPrice().formatIntegerCurrency());
			item.setText(c++, transfer.getPlayerValue().formatIntegerCurrency());

		}
		for (int i = 0; i < this.getColumnCount() - 1; i++) {
			this.getColumn(i).pack();
			this.getColumn(i).setWidth(this.getColumn(i).getWidth() + 5);
		}
		// Turn drawing back on
		this.setRedraw(true);
	}

	public void fill(List<Transfer> alTransfers) {
		fill(alTransfers, idClub);
	}

	public void sort(SVComparator<Transfer> comparator) {
		if (transfers != null) {
			Collections.sort(transfers, comparator);
			fill(transfers);
		}
	}

	public SVComparator<Transfer> getComparator() {
		return comparator;
	}

}
