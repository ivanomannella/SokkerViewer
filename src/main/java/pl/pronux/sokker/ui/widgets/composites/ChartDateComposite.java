package pl.pronux.sokker.ui.widgets.composites;

import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import pl.pronux.sokker.model.Date;

public class ChartDateComposite extends Composite {
	private Canvas canvas;

	private JFreeChart chart;

	private Frame chartFrame;

	private int column;

	private Composite comp;

	private ChartPanel cp;

//	private PaintListener paintListener;

	private NumberAxis rangeAxis;

	public ChartDateComposite(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new FormLayout());

		FormData canvasFormFill = new FormData();
		canvasFormFill.top = new FormAttachment(0, 0);
		canvasFormFill.left = new FormAttachment(0, 0);
		canvasFormFill.right = new FormAttachment(100, 0);
		canvasFormFill.bottom = new FormAttachment(100, 0);
		// canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE);
		// canvas.setVisible(false);

		// canvas.setLayoutData(canvasFormFill);

		comp = new Composite(this, SWT.EMBEDDED);
		comp.setLayoutData(canvasFormFill);

		chartFrame = SWT_AWT.new_Frame(comp);
		chartFrame.setLayout(new GridLayout());

	}

	public Canvas getCanvas() {
		return canvas;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void fillGraph(List<Number> values, List<Date> dates, final int trainingDay, final boolean zero, final double maxValue, boolean shapes, String title) {
		if (values.size() <= 0) {
			return;
		}

		final TimeSeries series = new TimeSeries("Weeks Periods", Day.class); 

		// GregorianCalendar date;
		for (int i = values.size() - 1; i >= 0; i--) {
			series.add(new Day(dates.get(i).getCalendar().getTime()), (Number) values.get(i));
		}

		setSeries(series, zero, maxValue, shapes, title);
	}

	public void fillGraph(List<Number> values, final String[] tempDateTable, final int trainingDay, final boolean zero, final int maxValue) {
		List<Date> dates = new ArrayList<Date>();

		for (int i = tempDateTable.length - 1; i >= 0; i--) {
			dates.add(new Date(tempDateTable[i].replaceAll("\\(.*\\)", "")));  
		}
		fillGraph(values, dates, trainingDay, zero, maxValue, true, ""); 
	}

	public void fillGraph(final double[] tempDoubleTable, final String[] tempDateTable, final int trainingDay, final boolean zero, final double maxValue) {
		List<Number> values = new ArrayList<Number>();
		List<Date> dates = new ArrayList<Date>();

		for (int i = tempDoubleTable.length - 1; i >= 0; i--) {
			dates.add(new Date(tempDateTable[i].replaceAll("\\(.*\\)", "")));  
			values.add(tempDoubleTable[i]);
		}
		fillGraph(values, dates, trainingDay, zero, maxValue, true, ""); 
	}

	/**
	 * 
	 * @param tempIntTable
	 * @param tempDateTable
	 * @param trainingDay
	 *          0 - saturday ... 6 - friday
	 */
	public void fillGraph(final int[] tempIntTable, final String[] tempDateTable, final int trainingDay, final boolean zero, final int maxValue) {
		List<Number> values = new ArrayList<Number>();
		List<Date> dates = new ArrayList<Date>();

		for (int i = tempIntTable.length - 1; i >= 0; i--) {
			dates.add(new Date(tempDateTable[i].replaceAll("\\(.*\\)", "")));  
			values.add(tempIntTable[i]);
		}
		fillGraph(values, dates, trainingDay, zero, maxValue, true, ""); 
	}

	private void setSeries(final TimeSeries series, final boolean zero, final double maxValue, boolean shapes, String title) {
		chartFrame.removeAll();
		final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
		chart = ChartFactory.createTimeSeriesChart(title, "", "", dataset, false, true, false);  
		
		final XYPlot plot = chart.getXYPlot();
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, shapes);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		if (zero) {
			rangeAxis.setAutoRangeIncludesZero(true);
		} else {
			rangeAxis.setAutoRangeIncludesZero(false);
		}

		if (maxValue != -1) {
			rangeAxis.setRange(-1, maxValue);
			rangeAxis.setAutoTickUnitSelection(false);
			java.awt.Font font = rangeAxis.getLabelFont();
			rangeAxis.setTickLabelFont(font.deriveFont(font.getStyle(), font.getSize() - 4));
		}
		drawChart(chart);

	}
	
	private void drawChart(JFreeChart chart) {
		cp = new ChartPanel(chart);
		chartFrame.add(cp);
		chartFrame.doLayout();
	}
	
	public void setMarkers(Date date, int day, Object value) {
		if (value instanceof Double) {
			setMarkers(date, day, (Double) value);
		} else if (value instanceof Integer) {
			setMarkers(date, day, (Integer) value);
		} else {
			return;
		}
	}

	public void setMarkers(Date date, int day, Integer range) {
		setMarkers(date, day, range.doubleValue());
	}

	public void setMarkers(Date date, int day, int range) {
		setMarkers(date, day, Double.valueOf(range));
	}

	public void setMarkers(Date date, int day, Double range) {

		Day q = null;
		chart.getXYPlot().clearDomainMarkers();
		if (day == -1) {
			q = new Day(date.getCalendar().getTime());
		} else {
			q = new Day(date.getTrainingDate(day).getCalendar().getTime());
		}

		ValueMarker vm = new ValueMarker(q.getFirstMillisecond());
		vm.setPaint(new java.awt.Color(0, 0, 255));

		chart.getXYPlot().addDomainMarker(vm);

		chart.getXYPlot().clearRangeMarkers();

		vm = new ValueMarker(range);
		vm.setPaint(new java.awt.Color(0, 0, 255));

		chart.getXYPlot().addRangeMarker(vm);
	}
}
