package pl.pronux.sokker.ui.widgets.tables;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pl.pronux.sokker.comparators.ExchangeComparator;
import pl.pronux.sokker.handlers.SettingsHandler;
import pl.pronux.sokker.interfaces.SVComparator;
import pl.pronux.sokker.model.Exchange;
import pl.pronux.sokker.model.Money;
import pl.pronux.sokker.resources.Messages;
import pl.pronux.sokker.ui.beans.ConfigBean;
import pl.pronux.sokker.ui.listeners.SortTableListener;
import pl.pronux.sokker.ui.resources.FlagsResources;
import pl.pronux.sokker.ui.widgets.interfaces.IViewSort;

public class ExchangeTable extends SVTable<Exchange> implements IViewSort<Exchange> {

	private ExchangeComparator comparator;
	private List<Exchange> exchanges;

	public ExchangeTable(Composite parent, int style) {
		super(parent, style);
		this.setLinesVisible(true);
		this.setHeaderVisible(true);
		this.setFont(ConfigBean.getFontTable());
		
		comparator = new ExchangeComparator();
		comparator.setColumn(ExchangeComparator.NAME);
		comparator.setDirection(ExchangeComparator.ASCENDING);

		String[] titles = {
				"", 
				Messages.getString("table.id"), 
				Messages.getString("exchange.country.name"), 
				Messages.getString("exchange.country.originalname"), 
				Messages.getString("exchange.currency"), 
				Messages.getString("exchange.value"), 
				"" 
		};

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(this, SWT.NONE);
			column.setText(titles[i]);
			column.setMoveable(false);
			column.setResizable(false);

			if (i == 5) {
				column.setAlignment(SWT.RIGHT);
			} else {
				column.setAlignment(SWT.LEFT);
			}

			if (titles[i].isEmpty()) {
				// column.setWidth(70);
				if (SettingsHandler.IS_LINUX) {
					column.pack();
				}
			} else {
				column.pack();
			}
		}
		
		this.setSortColumn(this.getColumn(ExchangeComparator.NAME));
		this.setSortDirection(SWT.UP);
		
		final TableColumn[] columns = this.getColumns();

		for (int i = 1; i < columns.length - 1; i++) {
			columns[i].addSelectionListener(new SortTableListener<Exchange>(this, comparator));
		}
	}
	
	public void fill(List<Exchange> alExchange) {
		this.setRedraw(false);
		this.exchanges = alExchange;
		this.remove(0, this.getItemCount()-1);

		Collections.sort(alExchange, comparator);
		for(Exchange exchange : alExchange) {
			TableItem item = new TableItem(this,SWT.NONE);
			item.setImage(FlagsResources.getFlag(exchange.getId()));
			item.setText(0, "" ); 
			item.setText(1, String.valueOf(exchange.getId()) );
			item.setText(2, exchange.getName());
			item.setText(3, exchange.getOriginalName());
			item.setText(4, exchange.getCurrency());
			item.setText(5, Money.formatDoubleCurrency(exchange.getValue()));
		}
		for(int i = 0 ; i < this.getColumnCount()-1; i++ ) {
			this.getColumn(i).pack();
		}

		this.setRedraw(true);
	}
	
	
	public void sort(SVComparator<Exchange> comparator) {
		if(exchanges != null) {
			Collections.sort(exchanges, comparator);
			fill(exchanges);
		}
		
	}

	public SVComparator<Exchange> getComparator() {
		return comparator;
	}


}
